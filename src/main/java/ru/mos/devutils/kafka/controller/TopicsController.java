package ru.mos.devutils.kafka.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.mos.devutils.kafka.client.KafkaTopic;
import ru.mos.devutils.kafka.service.KafkaService;

import java.util.List;

@Api(value = "/topics", description = "Просмотр топиков")
@RestController
@RequestMapping("${web.prefix}/topics")
public class TopicsController extends BaseController {

    private KafkaService kafkaService;

    @Autowired
    public TopicsController(KafkaService kafkaService) {
        this.kafkaService = kafkaService;
    }

    @ApiOperation(value = "Получить список топиков Kafka", response = KafkaTopic.class, responseContainer = "List")
    @GetMapping
    public List<KafkaTopic> getTopics(@ApiParam("Сервера кластера Kafka через запятую") @RequestHeader(value = "servers", required = false) String servers,
                                      @ApiParam("Security protocol") @RequestHeader(value = "security_protocol", required = false) String securityProtocol,
                                      @ApiParam("SASL механизм") @RequestHeader(value = "sasl_mechanism", required = false) String saslMechanism,
                                      @ApiParam("Login") @RequestHeader(value = "login", required = false) String login,
                                      @ApiParam("Password") @RequestHeader(value = "password", required = false) String password) {
        return kafkaService.getTopics(servers, securityProtocol, saslMechanism, login, password);
    }
}
