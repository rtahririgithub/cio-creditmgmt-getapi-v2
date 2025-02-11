package com.telus.credit;

import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.telus.credit.config.TracerConfig;

@SpringBootApplication
public class CreditProfileReadApiApplication {

	private static final Logger LOGGER = LoggerFactory.getLogger(CreditProfileReadApiApplication.class);

	public static void main(String[] args) throws IOException {
	
		SpringApplication.run(CreditProfileReadApiApplication.class, args);
		
		try {
			TracerConfig.createAndRegisterWithGCP();
			LOGGER.info("Tracer init done");			
			Properties prop = new Properties();
			prop.load(CreditProfileReadApiApplication.class.getClassLoader().getResourceAsStream("git.properties"));
			LOGGER.info("Git information: {}", prop);
		} catch (Exception e) {
			LOGGER.warn("Couldn't load git information {}", e.getMessage());
		}
	}

}
