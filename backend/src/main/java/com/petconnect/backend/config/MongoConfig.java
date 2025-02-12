//package com.petconnect.backend.config;
//
//import com.mongodb.client.MongoClients;
//import org.jetbrains.annotations.NotNull;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
//import org.springframework.data.mongodb.config.EnableMongoAuditing;
//import org.springframework.data.mongodb.core.MongoTemplate;
//import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
//
//@Configuration
//@EnableMongoAuditing
//@EnableMongoRepositories(basePackages = "com.petconnect.backend.repositories")
//public class MongoConfig extends AbstractMongoClientConfiguration {
//
//    @Value("${spring.data.mongodb.uri}")
//    private String mongoUri;
//
//    @NotNull
//    @Override
//    protected String getDatabaseName() {
//        return "petcare_connect";
//    }
//
//    @Bean
//    public MongoTemplate mongoTemplate() throws Exception {
//        return new MongoTemplate(MongoClients.create(mongoUri), getDatabaseName());
//    }
//}
