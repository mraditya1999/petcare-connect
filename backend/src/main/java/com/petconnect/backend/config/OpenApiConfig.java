package com.petconnect.backend.config;

import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("PetCare Connect API")
                        .version("v1")
                        .description("API documentation for PetCare Connect backend")
                        .contact(new Contact().name("PetCare Connect"))
                );
    }
}
