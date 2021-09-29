package de.ffm.rka.rkareddit.service;

import de.ffm.rka.rkareddit.domain.User;
import de.ffm.rka.rkareddit.domain.dto.UserDTO;
import de.ffm.rka.rkareddit.repository.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;

import java.util.Optional;

import static org.junit.Assert.assertTrue;



/**
 * Testclass for Servicelayer
 * @author kaproma
 *
 */
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@Transactional
@SpringBootTest
public class EmailServiceTest {

	//@Autowired
	private MailService mailService;
	
	@Autowired
    private UserRepository userRepository;

	@Autowired
	private ApplicationContext applicationContext;

	@Before
	public void setUp(){
		SpringTemplateEngine springTemplateEngine = new SpringTemplateEngine();
		SpringResourceTemplateResolver templateResolver = new SpringResourceTemplateResolver();
		templateResolver.setPrefix("classpath:/templates/");
		templateResolver.setSuffix(".html");
		templateResolver.setCharacterEncoding("UTF-8");
		templateResolver.setTemplateMode("HTML");
		templateResolver.setApplicationContext(applicationContext);
		springTemplateEngine.addTemplateResolver(templateResolver);

		mailService = new MailService(new JavaMailSenderImpl(), springTemplateEngine);

	}
	/**
	 * test for test_env for user on service-layer
	 */
	@Test
	public void linkSizeForUserOne() {
		Optional<User> user = userRepository.findByEmail("kaproma@yahoo.de");
		UserDTO userDto = UserDTO.mapUserToUserDto(user.get());
		String context = ReflectionTestUtils.invokeMethod(mailService, "createEmailContext", "localhost", userDto, "mail/new_email_activation");
		assertTrue("should contains dear baruch-david rka", context.contains("Dear baruc-david rka"));
	}
}
