package com.spring.petcareConnect;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableMongoRepositories(basePackages = "com.spring.petcareConnect.repositories.mongo")
@EnableJpaRepositories(basePackages = "com.spring.petcareConnect.repositories.jpa")
@SpringBootApplication
@EnableScheduling
public class PetcareConnectApplication {

	public static void main(String[] args) {
		SpringApplication.run(PetcareConnectApplication.class, args);
	}

}
