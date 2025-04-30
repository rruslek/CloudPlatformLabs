package com.example.imagicspringaws;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ImagicSpringAwsApplication {
	public static void main(String[] args) {
		SpringApplication.run(ImagicSpringAwsApplication.class, args);
	}
}