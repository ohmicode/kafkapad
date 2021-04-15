package ru.mos.devutils.kafka.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.mos.devutils.kafka.client.KafkaClient;
import ru.mos.devutils.kafka.client.KafkaMessage;
import ru.mos.devutils.kafka.client.KafkaTopic;

import java.util.List;

@Service
public class KafkaService {

    @Value("${default.kafka.servers}")
    private String KAFKA_SERVERS_DEFAULT;
    @Value("${default.kafka.sasl.config}")
    private String KAFKA_SASL_CONFIG_DEFAULT;
    @Value("${kafka.client.timeout:1000}")
    private long KAFKA_CLIENT_TIMEOUT;  // ms

    public List<KafkaTopic> getTopics(String servers, String securityProtocol, String saslMechanism, String login, String password) {
        KafkaClient kafkaClient = buildKafkaClient(servers, securityProtocol, saslMechanism, login, password);
        return kafkaClient.getTopics();
    }

    public List<KafkaMessage> getFirstMessages(String servers, String securityProtocol, String saslMechanism, String login, String password,
                                               String topicName, Integer partition, long messagesCount) {
        int partitionNumber = (partition == null) ? 0 : partition;
        KafkaClient kafkaClient = buildKafkaClient(servers, securityProtocol, saslMechanism, login, password);
        long offset = kafkaClient.findFirstOffset(topicName, partitionNumber);
        return readMessagesFromClient(kafkaClient, topicName, partitionNumber, offset, messagesCount);
    }

    public List<KafkaMessage> getLastMessages(String servers, String securityProtocol, String saslMechanism, String login, String password,
                                              String topicName, Integer partition, long messagesCount) {
        int partitionNumber = (partition == null) ? 0 : partition;
        KafkaClient kafkaClient = buildKafkaClient(servers, securityProtocol, saslMechanism, login, password);
        return readMessagesFromClient(kafkaClient, topicName, partitionNumber, -messagesCount, messagesCount);
    }

    public List<KafkaMessage> getMessagesFromOffset(String servers, String securityProtocol, String saslMechanism, String login, String password,
                                                    String topicName, Integer partition, long offset, long messagesCount) {
        int partitionNumber = (partition == null) ? 0 : partition;
        KafkaClient kafkaClient = buildKafkaClient(servers, securityProtocol, saslMechanism, login, password);
        return readMessagesFromClient(kafkaClient, topicName, partitionNumber, offset, messagesCount);
    }

    public boolean sendMessage(String servers, String securityProtocol, String saslMechanism, String login, String password,
                            String topicName, Integer partition, String key, String message) {
        int partitionNumber = (partition == null) ? 0 : partition;
        KafkaClient kafkaClient = buildKafkaClient(servers, securityProtocol, saslMechanism, login, password);
        return kafkaClient.send(topicName, partitionNumber, key, message, KAFKA_CLIENT_TIMEOUT);
    }

    private KafkaClient buildKafkaClient(String servers, String securityProtocol, String saslMechanism, String login, String password) {
        String cluster = (servers == null) ? KAFKA_SERVERS_DEFAULT : servers;
        String saslConfig = String.format(KAFKA_SASL_CONFIG_DEFAULT, login, password);
        return new KafkaClient(cluster, securityProtocol, saslMechanism, saslConfig);
    }

    private List<KafkaMessage> readMessagesFromClient(KafkaClient kafkaClient, String topicName, Integer partition, long offset, long messagesCount) {
        kafkaClient.listenToTopic(topicName, partition, offset, messagesCount);
        kafkaClient.start();

        try {
            kafkaClient.awaitMessages(KAFKA_CLIENT_TIMEOUT);
        } catch (InterruptedException unexpected) {
            //TODO: log error
        }

        kafkaClient.stop();
        return kafkaClient.getMessages();
    }
}
