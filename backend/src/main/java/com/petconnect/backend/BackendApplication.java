package com.petconnect.backend;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.security.Key;
import java.util.Base64;

@SpringBootApplication
public class BackendApplication {

	public static void main(String[] args) {


		Key secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS512);
		String base64EncodedKey = Base64.getEncoder().encodeToString(secretKey.getEncoded());
		System.out.println(base64EncodedKey);
		SpringApplication.run(BackendApplication.class, args);
	}

}
