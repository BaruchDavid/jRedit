package de.ffm.rka.rkareddit;


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
	public static void main(String[] args) {
		LOGGER.info("START JREDITT APPLICATION");
		SpringApplication.run(RkaRedditApplication.class, args);
	}
	
}
