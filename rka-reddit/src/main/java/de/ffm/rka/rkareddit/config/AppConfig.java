package de.ffm.rka.rkareddit.config;

import org.ocpsoft.prettytime.PrettyTime;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AppConfig {

	@Bean
	public PrettyTime getPrettyTime() {
		return new PrettyTime();
	}
	

	@Bean
	public PasswordEncoder passwordEncoder() {
	    return new BCryptPasswordEncoder();
	}

	

}
