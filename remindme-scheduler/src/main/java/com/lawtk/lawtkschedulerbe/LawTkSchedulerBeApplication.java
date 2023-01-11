package com.lawtk.lawtkschedulerbe;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableRabbit
@SpringBootApplication
public class LawTkSchedulerBeApplication {

	public static void main(String[] args) {
		SpringApplication.run(LawTkSchedulerBeApplication.class, args);
	}

}
