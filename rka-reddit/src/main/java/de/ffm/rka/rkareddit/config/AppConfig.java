package de.ffm.rka.rkareddit.config;

import de.ffm.rka.rkareddit.interceptor.ApplicationHandlerInterceptor;
import org.modelmapper.ModelMapper;
import org.ocpsoft.prettytime.PrettyTime;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.thymeleaf.extras.springsecurity5.dialect.SpringSecurityDialect;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableAsync
public class AppConfig implements WebMvcConfigurer{

	@Bean
	public PrettyTime getPrettyTime() {
		return new PrettyTime();
	}
	
	@Bean
	public ModelMapper getModelMapper() {
		return new ModelMapper();
	}
	

	@Bean
	public PasswordEncoder passwordEncoder() {
	    return new BCryptPasswordEncoder();
	}

	
	@Bean
	public SpringSecurityDialect securityDialectThymeleaf() {
		return new SpringSecurityDialect();
	}
	
	@Bean
	@Profile("test")
	public DatabaseLoader databaseLoader() {
		return new DatabaseLoader();
	}

	/**
	 * @return spring-bean for converting pictures to bytearray for browser
	 */
	@Bean
	public ByteArrayHttpMessageConverter byteArrayHttpMessageConverter() {
	    ByteArrayHttpMessageConverter arrayHttpMessageConverter = new ByteArrayHttpMessageConverter();
	    arrayHttpMessageConverter.setSupportedMediaTypes(getSupportedMediaTypes());
	    return arrayHttpMessageConverter;
	}

	private List<MediaType> getSupportedMediaTypes() {
	    List<MediaType> list = new ArrayList<>();
	    list.add(MediaType.IMAGE_JPEG);
	    list.add(MediaType.IMAGE_PNG);
	    list.add(MediaType.APPLICATION_OCTET_STREAM);
	    return list;
	}


	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new ApplicationHandlerInterceptor());
		WebMvcConfigurer.super.addInterceptors(registry);
	}
	
	

}
