package ru.mos.devutils.kafka.client;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.Node;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.listener.config.ContainerProperties;
import org.springframework.kafka.support.ProducerListener;
import org.springframework.kafka.support.TopicPartitionInitialOffset;

import java.util.*;
import java.util.concurrent.*;

import static org.apache.kafka.clients.CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG;
import static org.apache.kafka.clients.CommonClientConfigs.SECURITY_PROTOCOL_CONFIG;

public class KafkaClient {

    private ConsumerFactory<String, String> kafkaConsumerFactory;
    private ProducerFactory<String, String> kafkaProducerFactory;
    private ConcurrentMessageListenerContainer<String, String> container;

    private long expectedMessagesCount;
    private CountDownLatch listenerBarrier = new CountDownLatch(1);

    private List<KafkaMessage> messages = new CopyOnWriteArrayList<>();
    private List<Exception> exceptions = new CopyOnWriteArrayList<>();


    public KafkaClient(String servers, String securityProtocol, String saslMechanism, String saslConfig) {
        Map<String, Object> kafkaConfig = new HashMap<>();
        kafkaConfig.put(BOOTSTRAP_SERVERS_CONFIG, servers);
        if (!isAbsent(securityProtocol)) {
            kafkaConfig.put(SECURITY_PROTOCOL_CONFIG, securityProtocol);
        }
        if (!isAbsent(saslMechanism)) {
            kafkaConfig.put("sasl.mechanism", saslMechanism);
        }
        if (!isAbsent(saslConfig)) {
            kafkaConfig.put("sasl.jaas.config", saslConfig);
        }

        kafkaConsumerFactory = new DefaultKafkaConsumerFactory<>(
                kafkaConfig,
                new StringDeserializer(),
                new StringDeserializer());
        kafkaProducerFactory = new DefaultKafkaProducerFactory<>(
                kafkaConfig,
                new StringSerializer(),
                new StringSerializer());
    }

    public List<KafkaTopic> getTopics() {
        Map<String, List<PartitionInfo>> topicsInfo = getTopicsInfo(kafkaConsumerFactory);
        return transformTopicsInfo(topicsInfo);
    }

    public long findFirstOffset(String topicName, int partition) {
        try (Consumer<String, String> consumer = kafkaConsumerFactory.createConsumer()) {
            TopicPartition topicPartition = new TopicPartition(topicName, partition);
            Map<TopicPartition, Long> offsetsByPartition = consumer.beginningOffsets(Collections.singletonList(topicPartition));

            return offsetsByPartition
                    .entrySet()
                    .stream()
                    .filter(entry -> entry.getKey().partition() == partition)
                    .findFirst()
                    .map(Map.Entry::getValue)
                    .orElse(0L);
        }
    }

    public void listenToTopic(String topicName, int partition, long offset, long count) {
        expectedMessagesCount = count;
        listenerBarrier = new CountDownLatch(1);

        TopicPartitionInitialOffset topic = new TopicPartitionInitialOffset(topicName, partition, offset, false);
        ContainerProperties containerProperties = new ContainerProperties(topic);
        containerProperties.setGroupId(null);
        containerProperties.setErrorHandler((thrownException, data) -> {
            exceptions.add(thrownException);
            listenerBarrier.countDown();
        });
        containerProperties.setMessageListener(
                (MessageListener<String, String>) record -> {
                    if (messages.size() < expectedMessagesCount) {
                        KafkaMessage message = new KafkaMessage(record.timestamp(),
                                record.timestampType().name,
                                transformHeaders(record.headers()),
                                record.partition(),
                                record.offset(),
                                record.key(),
                                record.value());
                        messages.add(message);
                    }

                    if (messages.size() >= expectedMessagesCount) {
                        listenerBarrier.countDown();
                    }
                });

        container = new ConcurrentMessageListenerContainer<>(
                kafkaConsumerFactory,
                containerProperties);
    }

    public void start() {
        if (container == null) {
            throw new IllegalStateException("Please call listenToTopic() before start() KafkaClient");
        }
        container.start();
    }

    public void stop() {
        if (container != null) {
            container.stop();
        }
    }

    public void awaitMessages(long timeout) throws InterruptedException {
        listenerBarrier.await(timeout, TimeUnit.MILLISECONDS);
    }

    public List<KafkaMessage> getMessages() {
        if (exceptions.isEmpty()) {
            return messages;
        } else {
            // we can throw only one exception
            throw wrapException(exceptions.get(0));
        }
    }

    public boolean send(String topic, Integer partition, String key, String message, long timeout) {
        final CountDownLatch producerBarrier = new CountDownLatch(1);
        final Queue<Exception> producerExceptions = new ConcurrentLinkedQueue<>();

        KafkaTemplate<String, String> kafkaTemplate = new KafkaTemplate<>(kafkaProducerFactory);
        kafkaTemplate.setProducerListener(new ProducerListener<String, String>() {
            @Override
            public void onSuccess(String topic, Integer partition, String key, String value, RecordMetadata recordMetadata) {
                producerBarrier.countDown();
            }

            @Override
            public void onError(String topic, Integer partition, String key, String value, Exception exception) {
                producerExceptions.add(exception);
                producerBarrier.countDown();
            }

            @Override
            public boolean isInterestedInSuccess() {
                return true;
            }
        });

        kafkaTemplate.send(topic, partition, key, message);
        try {
            producerBarrier.await(timeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException ie) {
            throw new KafkaClientExceptionWrapper(ie);
        }

        if (!producerExceptions.isEmpty()) {
            // we can throw only one exception
            throw wrapException(producerExceptions.poll());
        } else if (producerBarrier.getCount() == 0) {
            // this is successful result
            return true;
        } else {
            // timeout happened
            throw new KafkaClientExceptionWrapper(new TimeoutException("Sending message: timeout " + timeout + "ms exceeded"));
        }
    }

    private Map<String, String> transformHeaders(Headers headers) {
        Map<String, String> transformed = new HashMap<>();
        for(Header header : headers) {
            String value = Arrays.toString(header.value());
            transformed.put(header.key(), value);
        }
        return transformed;
    }

    private List<KafkaTopic> transformTopicsInfo(Map<String, List<PartitionInfo>> map) {
        List<KafkaTopic> topics = new ArrayList<>();
        map.forEach((name, partitionsInfo) -> {
            List<KafkaPartition> partitions = new ArrayList<>();
            partitionsInfo.forEach(info -> partitions.add(transformPartitionInfo(info)));
            KafkaTopic topic = new KafkaTopic(name, partitions);
            topics.add(topic);
        });
        return topics;
    }

    private KafkaPartition transformPartitionInfo(PartitionInfo info) {
        KafkaNode leader = new KafkaNode(info.leader().id(), info.leader().host(), info.leader().port(), info.leader().rack());
        List<KafkaNode> replicas = new ArrayList<>();
        for(Node node : info.replicas()) {
            replicas.add(new KafkaNode(node.id(), node.host(), node.port(), node.rack()));
        }
        List<KafkaNode> inSync = new ArrayList<>();
        for(Node node : info.inSyncReplicas()) {
            inSync.add(new KafkaNode(node.id(), node.host(), node.port(), node.rack()));
        }
        List<KafkaNode> offline = new ArrayList<>();
        for(Node node : info.offlineReplicas()) {
            offline.add(new KafkaNode(node.id(), node.host(), node.port(), node.rack()));
        }
        return new KafkaPartition(info.partition(), leader, replicas, inSync, offline);
    }

    private Map<String, List<PartitionInfo>> getTopicsInfo(ConsumerFactory<String, String> kafkaConsumerFactory) {
        try (Consumer<String, String> consumer =
                     kafkaConsumerFactory.createConsumer()) {
            return consumer.listTopics();
        }
    }

    private RuntimeException wrapException(Exception exception) {
        if (exception instanceof RuntimeException) {
            return (RuntimeException) exception;
        } else {
            return new KafkaClientExceptionWrapper(exception);
        }
    }

    private boolean isAbsent(String s) {
        return (s == null) || s.trim().isEmpty();
    }
}
