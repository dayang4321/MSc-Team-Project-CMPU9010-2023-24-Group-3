package com.docparser.springboot;

// Importing necessary classes from the Spring Boot framework
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// Annotation to mark this class as a Spring Boot Application
@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		// Running the Spring Boot application
		// This method will bootstrap the application, starting the Spring Application
		// Context
		SpringApplication.run(Application.class, args);
	}

}
