package de.ffm.rka.rkareddit.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
/**
 * 
 * @author RKA: configures several jpa issues
 *
 */
@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class AppJpaConfig {

	@Bean
	public AuditorAware<String> auditorAware(){
		return new AuditorAwareImpl();
	}
}
