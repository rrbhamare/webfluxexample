package com.example.demowebflux;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class DemowebfluxApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemowebfluxApplication.class, args);
	}

}
