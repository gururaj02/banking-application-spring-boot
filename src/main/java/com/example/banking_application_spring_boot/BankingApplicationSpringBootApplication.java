package com.example.banking_application_spring_boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class BankingApplicationSpringBootApplication {

	public static void main(String[] args) {
		SpringApplication.run(BankingApplicationSpringBootApplication.class, args);
	}

}
