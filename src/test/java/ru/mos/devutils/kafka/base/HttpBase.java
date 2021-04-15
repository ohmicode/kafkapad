package ru.mos.devutils.kafka.base;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.apache.kafka.common.errors.SaslAuthenticationException;
import org.apache.kafka.common.errors.TopicAuthorizationException;
import ru.mos.devutils.kafka.client.KafkaMessage;
import ru.mos.devutils.kafka.client.KafkaNode;
import ru.mos.devutils.kafka.client.KafkaPartition;
import ru.mos.devutils.kafka.client.KafkaTopic;
import ru.mos.devutils.kafka.controller.QueueController;
import ru.mos.devutils.kafka.controller.TopicsController;
import ru.mos.devutils.kafka.service.KafkaService;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.apache.kafka.common.errors.AuthenticationException;
import org.apache.kafka.common.errors.AuthorizationException;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.test.web.servlet.setup.StandaloneMockMvcBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public abstract class HttpBase {

    private StandaloneMockMvcBuilder controllers(Object... controllers) {
        return MockMvcBuilders
                       .standaloneSetup(controllers)
                       .addPlaceholderValue("web.prefix", "/kafka");
        // .setMessageConverters(...);
    }


    @Before
    public void setup() {
        KafkaService kafkaService = mockKafkaService();

        TopicsController topicsController = new TopicsController(kafkaService);
        QueueController queueController = new QueueController(kafkaService);

        RestAssuredMockMvc.standaloneSetup(controllers(topicsController, queueController));
    }

    private KafkaService mockKafkaService() {
        KafkaService kafkaService = mock(KafkaService.class);

        when(kafkaService.getTopics(any(), any(), any(), any(), any()))
                .thenReturn(generateTopics());

        List<KafkaMessage> threeMessagesTopic = generateMessages(3);
        when(kafkaService.getLastMessages(any(), any(), any(), any(), any(), eq("three.messages.topic"), any(), eq(3L)))
                .thenReturn(threeMessagesTopic);
        when(kafkaService.getFirstMessages(any(), any(), any(), any(), any(), eq("three.messages.topic"), any(), eq(2L)))
                .thenReturn(Arrays.asList(threeMessagesTopic.get(0), threeMessagesTopic.get(1)));
        when(kafkaService.getMessagesFromOffset(any(), any(), any(), any(), any(), eq("three.messages.topic"), any(), eq(1L), eq(1L)))
                .thenReturn(Collections.singletonList(threeMessagesTopic.get(1)));
        when(kafkaService.getFirstMessages(any(), any(), any(), any(), any(), eq("empty.topic"), any(), anyLong()))
                .thenReturn(Collections.emptyList());
        when(kafkaService.sendMessage(any(), any(), any(), any(), any(), anyString(), any(), any(), anyString()))
                .thenReturn(true);

        when(kafkaService.getTopics(any(), any(), any(), eq("CorrectLogin"), eq("WrongPassword")))
                .thenThrow(new AuthenticationException("Wrong password"));
        when(kafkaService.getLastMessages(any(), any(), any(), any(), any(), eq("forbidden.topic"), any(), anyLong()))
                .thenThrow(new AuthorizationException("Cant access forbidden topic"));
        when(kafkaService.sendMessage(any(), any(), any(), eq("CorrectLogin"), eq("WrongPassword"), anyString(), any(), any(), anyString()))
                .thenThrow(new SaslAuthenticationException("Wrong password"));
        when(kafkaService.sendMessage(any(), any(), any(), any(), any(), eq("forbidden.topic"), any(), any(), anyString()))
                .thenThrow(new TopicAuthorizationException("Cant access forbidden topic"));

        return kafkaService;
    }

    private List<KafkaTopic> generateTopics() {
        KafkaNode node = new KafkaNode(0, "127.0.0.1", 7777, null);
        KafkaPartition partition = new KafkaPartition(0, node, Collections.singletonList(node), Collections.singletonList(node), Collections.emptyList());
        KafkaTopic topic1 = new KafkaTopic("three.messages.topic", Collections.singletonList(partition));
        KafkaTopic topic2 = new KafkaTopic("empty.topic", Collections.singletonList(partition));
        return Arrays.asList(topic1, topic2);
    }

    private List<KafkaMessage> generateMessages(int count) {
        List<KafkaMessage> messages = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            KafkaMessage message = new KafkaMessage(42L, "creationTime", new HashMap<String, String>(), 0, i, null, "message" + i);
            messages.add(message);
        }
        return messages;
    }
}
