package com.letschat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;

@SpringBootApplication
@EnableWebSocketMessageBroker
@EnableAsync
public class LetsChatApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(LetsChatApplication.class, args);
	}
	
}
