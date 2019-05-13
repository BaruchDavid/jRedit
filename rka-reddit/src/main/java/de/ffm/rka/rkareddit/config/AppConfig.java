package de.ffm.rka.rkareddit.config;

import org.ocpsoft.prettytime.PrettyTime;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//@Configuration
public class AppConfig {

	@Bean
	public PrettyTime getPrettyTime() {
		return new PrettyTime();
	}
	
}
