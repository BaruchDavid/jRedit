package de.ffm.rka.rkareddit;


import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
@EnableCaching
public class RkaRedditApplication {

	private static final Logger LOGGER = LoggerFactory.getLogger(RkaRedditApplication.class);
	private static final String PROD = "prod";
	private static final String DEV = "dev";
	private static final String TEST = "test";
	
	public static void main(String[] args) {
		LOGGER.info("START JREDITT APPLICATION with ARGS {}", List.of(args));
		String environment = args[0].substring(args[0].indexOf('=')+1, args[0].length());
		if(!PROD.equals(environment) 
			&& !DEV.equals(environment)
			&& !TEST.equalsIgnoreCase(environment)) {
			throw new IllegalArgumentException("INVALID RUNTIME-ENVIRONMENT: ".concat(environment));
		}
		SpringApplication.run(RkaRedditApplication.class, args);
	}
	
}
