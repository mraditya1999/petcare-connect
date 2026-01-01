package com.petconnect.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.retry.annotation.EnableRetry;

@EnableMongoRepositories(basePackages = "com.petconnect.backend.repositories.mongo")
@EnableJpaRepositories(basePackages = "com.petconnect.backend.repositories.jpa")
@EnableMongoAuditing
@EnableRetry
@SpringBootApplication
public class BackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }

}