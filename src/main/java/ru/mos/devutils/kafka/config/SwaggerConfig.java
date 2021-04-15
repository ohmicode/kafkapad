package ru.mos.devutils.kafka.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
@ComponentScan(basePackages = {"ru.mos.devutils.kafka"})
public class SwaggerConfig {

    @Value("${web.prefix}")
    private String WEB_PREFIX;

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(path -> path.startsWith(WEB_PREFIX))
                .build()
                .apiInfo(apiInfo());
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Devutils Kafka tool")
                .termsOfServiceUrl("")
                .contact(new Contact("Devutils", "devutils.edu.mos.ru", "devutils@it.mos.ru"))
                .license("")
                .licenseUrl("")
                .version("1.0")
                .build();
    }
}
