package de.ffm.rka.rkareddit.config;

import java.util.ArrayList;
import java.util.List;

import org.ocpsoft.prettytime.PrettyTime;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.thymeleaf.extras.springsecurity5.dialect.SpringSecurityDialect;

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

	
	@Bean
	public SpringSecurityDialect securitydialectThymeleafSec5() {
		return new SpringSecurityDialect();
	}
	
	@Bean
	@Profile("test")
	public DatabaseLoader databaseLoader() {
		return new DatabaseLoader();
	}

	/**
	 * spring-bean for converting pictures to bytearray for browser
	 * @return
	 */
	@Bean
	public ByteArrayHttpMessageConverter byteArrayHttpMessageConverter() {
	    ByteArrayHttpMessageConverter arrayHttpMessageConverter = new ByteArrayHttpMessageConverter();
	    arrayHttpMessageConverter.setSupportedMediaTypes(getSupportedMediaTypes());
	    return arrayHttpMessageConverter;
	}

	private List<MediaType> getSupportedMediaTypes() {
	    List<MediaType> list = new ArrayList<MediaType>();
	    list.add(MediaType.IMAGE_JPEG);
	    list.add(MediaType.IMAGE_PNG);
	    list.add(MediaType.APPLICATION_OCTET_STREAM);
	    return list;
	}

}
