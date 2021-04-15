package ru.mos.devutils.kafka;

import io.swagger.models.Swagger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import ru.mos.devutils.kafka.config.SwaggerConfig;

@SpringBootApplication(exclude = KafkaAutoConfiguration.class)
public class KafkaPadApplication {

    public static void main(String[] args) {
        SpringApplication.run(new Class[]{KafkaPadApplication.class, Swagger.class, SwaggerConfig.class}, args);
    }
}
