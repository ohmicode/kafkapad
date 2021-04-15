package ru.mos.devutils.kafka.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.mos.devutils.kafka.client.KafkaMessage;
import ru.mos.devutils.kafka.service.KafkaService;

import java.util.*;

@Api(value = "/messages", description = "Просмотр сообщений в топике")
@RestController
@RequestMapping("${web.prefix}/messages")
public class QueueController extends BaseController {

    private KafkaService kafkaService;

    @Autowired
    public QueueController(KafkaService kafkaService) {
        this.kafkaService = kafkaService;
    }

    @ApiOperation(value = "Получить сообщения с начала топика Kafka", response = KafkaMessage.class, responseContainer = "List")
    @GetMapping("/first")
    public List<KafkaMessage> getFirstMessages(@ApiParam("Сервера кластера Kafka через запятую") @RequestHeader(value = "servers", required = false) String servers,
                                               @ApiParam("Security protocol") @RequestHeader(value = "security_protocol", required = false) String securityProtocol,
                                               @ApiParam("SASL механизм") @RequestHeader(value = "sasl_mechanism", required = false) String saslMechanism,
                                               @ApiParam("Login") @RequestHeader(value = "login", required = false) String login,
                                               @ApiParam("Password") @RequestHeader(value = "password", required = false) String password,
                                               @ApiParam("Название топика") @RequestParam(value = "topic", required = true) String topic,
                                               @ApiParam("Partition") @RequestParam(value = "partition", required = false) Integer partition,
                                               @ApiParam("Количество сообщений") @RequestParam(value = "count", required = true) Long count) {
        return kafkaService.getFirstMessages(servers, securityProtocol, saslMechanism, login, password, topic, partition, count);
    }

    @ApiOperation(value = "Получить сообщения с конца топика Kafka", response = KafkaMessage.class, responseContainer = "List")
    @GetMapping("/last")
    public List<KafkaMessage> getLastMessages(@ApiParam("Сервера кластера Kafka через запятую") @RequestHeader(value = "servers", required = false) String servers,
                                              @ApiParam("Security protocol") @RequestHeader(value = "security_protocol", required = false) String securityProtocol,
                                              @ApiParam("SASL механизм") @RequestHeader(value = "sasl_mechanism", required = false) String saslMechanism,
                                              @ApiParam("Login") @RequestHeader(value = "login", required = false) String login,
                                              @ApiParam("Password") @RequestHeader(value = "password", required = false) String password,
                                              @ApiParam("Название топика") @RequestParam(value = "topic", required = true) String topic,
                                              @ApiParam("Partition") @RequestParam(value = "partition", required = false) Integer partition,
                                              @ApiParam("Количество сообщений") @RequestParam(value = "count", required = true) Long count) {
        return kafkaService.getLastMessages(servers, securityProtocol, saslMechanism, login, password, topic, partition, count);
    }

    @ApiOperation(value = "Получить сообщения из топика Kafka с конкретной позиции", response = KafkaMessage.class, responseContainer = "List")
    @GetMapping("/direct")
    public List<KafkaMessage> getDirectMessages(@ApiParam("Сервера кластера Kafka через запятую") @RequestHeader(value = "servers", required = false) String servers,
                                                @ApiParam("Security protocol") @RequestHeader(value = "security_protocol", required = false) String securityProtocol,
                                                @ApiParam("SASL механизм") @RequestHeader(value = "sasl_mechanism", required = false) String saslMechanism,
                                                @ApiParam("Login") @RequestHeader(value = "login", required = false) String login,
                                                @ApiParam("Password") @RequestHeader(value = "password", required = false) String password,
                                                @ApiParam("Название топика") @RequestParam(value = "topic", required = true) String topic,
                                                @ApiParam("Partition") @RequestParam(value = "partition", required = false) Integer partition,
                                                @ApiParam("Начиная с позиции") @RequestParam(value = "offset", required = true) Long offset,
                                                @ApiParam("Количество сообщений") @RequestParam(value = "count", required = true) Long count) {
        return kafkaService.getMessagesFromOffset(servers, securityProtocol, saslMechanism, login, password, topic, partition, offset, count);
    }

    @ApiOperation(value = "Отправить сообщение в указанный топик Kafka", response = String.class)
    @PostMapping(value = "/send", consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    public String sendMessage(@ApiParam("Сервера кластера Kafka через запятую") @RequestHeader(value = "servers", required = false) String servers,
                              @ApiParam("Security protocol") @RequestHeader(value = "security_protocol", required = false) String securityProtocol,
                              @ApiParam("SASL механизм") @RequestHeader(value = "sasl_mechanism", required = false) String saslMechanism,
                              @ApiParam("Login") @RequestHeader(value = "login", required = false) String login,
                              @ApiParam("Password") @RequestHeader(value = "password", required = false) String password,
                              @ApiParam("Название топика") @RequestParam(value = "topic", required = true) String topic,
                              @ApiParam("Partition") @RequestParam(value = "partition", required = false) Integer partition,
                              @ApiParam("Key") @RequestParam(value = "key", required = false) String key,
                              @ApiParam("Сообщение") @RequestBody String message) {
        boolean success = kafkaService.sendMessage(servers, securityProtocol, saslMechanism, login, password, topic, partition, key, message);
        return success ? "Message sent" : "Error sending message";
    }
}
