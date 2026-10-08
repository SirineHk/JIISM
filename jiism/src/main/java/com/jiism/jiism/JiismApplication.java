package com.jiism.jiism;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class JiismApplication {

	public static void main(String[] args) {
		SpringApplication.run(JiismApplication.class, args);
	}

}
