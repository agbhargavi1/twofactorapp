package com.bhargavi.portal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

import com.bhargavi.portal.model.Greeting;
import com.bhargavi.portal.model.HelloMessage;

@SpringBootApplication
@Controller
public class PortalApplication {

	public static void main(String[] args) {
		SpringApplication.run(PortalApplication.class, args);
	}

	@RequestMapping(value = { "/index", "/home", "/welcome", "/", "/login" })
	public String home() {
		return "index";
	}
	
	@RequestMapping(value = { "/dashboard" })
	public String dashboard() {
		return "dashboard";
	}
	
	@RequestMapping(value = { "/register" })
	public String register() {
		return "register";
	}

	@Bean
	public RestTemplate getRestTemplate() {
		return new RestTemplate();
	}

	@MessageMapping("/hello")
	@SendTo("/topic/greetings")
	public Greeting greeting(HelloMessage message) throws Exception {
		System.out.println("message:" + message.getName());
		Thread.sleep(1000); // simulated delay
		return new Greeting(message.getName());
	}

}

