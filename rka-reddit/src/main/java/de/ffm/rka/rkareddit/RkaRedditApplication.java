package de.ffm.rka.rkareddit;

import org.ocpsoft.prettytime.PrettyTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import net.bytebuddy.asm.Advice.This;

@SpringBootApplication
public class RkaRedditApplication {

	private static final Logger LOGGER = LoggerFactory.getLogger(RkaRedditApplication.class);
	public static void main(String[] args) {
		LOGGER.info("Info Message");
		SpringApplication.run(RkaRedditApplication.class, args);
	}
	
}
