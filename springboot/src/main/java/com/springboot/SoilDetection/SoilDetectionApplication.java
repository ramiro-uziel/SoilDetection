package com.springboot.SoilDetection;

import io.github.cdimascio.dotenv.Dotenv;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;

@SpringBootApplication(exclude = HibernateJpaAutoConfiguration.class)
public class SoilDetectionApplication {

	private static final Logger logger = LoggerFactory.getLogger(SoilDetectionApplication.class);
	private static Dotenv dotenv;

	public static void main(String[] args) {
		dotenv = Dotenv.configure().load();

		logger.debug("Loading environment variables:");
		dotenv.entries().forEach(entry -> {
			String key = entry.getKey();
			String value = entry.getValue();
			System.setProperty(key, value);
			String maskedValue = key.toLowerCase().contains("token") || key.toLowerCase().contains("password")
					|| key.toLowerCase().contains("secret") ? "********" : value;
			logger.debug("{}={}", key, maskedValue);
		});

		SpringApplication.run(SoilDetectionApplication.class, args);
	}
}