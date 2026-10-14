package com.example.ordervalidation.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Online Shopping Order Service - Request Validation API")
                        .version("1.0.0")
                        .description("Demonstrates robust REST API request validation in Spring Boot 3 using Jakarta Bean Validation, " +
                                     "custom `@RestControllerAdvice` error responses, and Swagger UI documentation.")
                        .contact(new Contact()
                                .name("Development Team")
                                .email("dev@example.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://spring.io/projects/spring-boot")));
    }
}
