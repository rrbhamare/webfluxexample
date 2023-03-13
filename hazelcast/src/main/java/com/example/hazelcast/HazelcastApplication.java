package com.example.hazelcast;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import reactor.blockhound.BlockHound;

@SpringBootApplication
public class HazelcastApplication {

	public static void main(String[] args) {
		BlockHound.install();
		SpringApplication.run(HazelcastApplication.class, args);
	}

}
