package com.ztidentity.testservice2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class Testservice2Application {

	public static void main(String[] args) {
		SpringApplication.run(Testservice2Application.class, args);
	}

	@GetMapping("/hello")
	public String hello(@RequestParam(value = "name", defaultValue = "World") String name) {
		return String.format("Hello %s!", name);
	}
	
	@GetMapping("/invoice")
	public String getInvoice(
			@RequestHeader(name = "Authorization", required = false) String authHeader
			) {
		if (authHeader == null) {
			return "not_available";
		} else {
			if (authHeader.equals("user1")) {
				return "invoice1";
			} else if (authHeader.equals("user2")) {
				return "invoice2";
			} else {
				return "user_unknown";
			}
		}
	}	
}
	