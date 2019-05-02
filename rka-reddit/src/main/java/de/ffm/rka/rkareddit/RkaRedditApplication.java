package de.ffm.rka.rkareddit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import net.bytebuddy.asm.Advice.This;

@SpringBootApplication
public class RkaRedditApplication {

	private static final Logger LOGGER = LoggerFactory.getLogger(RkaRedditApplication.class);
	public static void main(String[] args) {
		LOGGER.info("Info Message");
		SpringApplication.run(RkaRedditApplication.class, args);
	}

}
