package com.example.charity_app_v1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.example.charity_app_v1")
public class CharityAppV1Application {

	public static void main(String[] args) {
		SpringApplication.run(CharityAppV1Application.class, args);
	}

}
