package de.ffm.rka.rkareddit.service;

import java.util.Locale;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.junit.Ignore;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import de.ffm.rka.rkareddit.domain.User;
import de.ffm.rka.rkareddit.domain.dto.UserDTO;

/**
 * sending several emails from tempalate
 * @author RKA
 *
 */
@Service
public class MailService {
	private static final String FAIL_TO_SEND = "FAIL TO SEND EMAIL";
	private static final Logger LOGGER = LoggerFactory.getLogger(MailService.class);
	private final JavaMailSender mailSender;
	private ModelMapper modelMapper;
	private final SpringTemplateEngine templateEngine;
	

	@Value("${linkMeEmailService}")
	private String BASE_URL;
	public MailService(JavaMailSender mailSender, ModelMapper modelMapper, SpringTemplateEngine templateEngine) {
		super();
		this.mailSender = mailSender;
		this.templateEngine = templateEngine;
	}
	
	/**
	 * sends emal ansychron
	 */
	@Async
	public void sendEmail(String to, String subject, String content, boolean isMultipart, boolean isHtml) {
		LOGGER.info("TRY SEND EMAIL TO {} witch Subject {}", to, subject);
		try {
			MimeMessage mimeMessage = this.mailSender.createMimeMessage();
			MimeMessageHelper message = new MimeMessageHelper(mimeMessage, "UTF-8");
			message.setTo(to);
			message.setFrom("noreplay@jReditt.com");
			message.setSubject(subject);
			message.setText(content,isHtml);
			mailSender.send(mimeMessage);
			LOGGER.info("SEND REGISTRATION MAIL SUCCESSFULLY TO {}", mimeMessage.getAllRecipients());
		} catch (MailException e) {
			LOGGER.error(FAIL_TO_SEND, e);
		} catch (MessagingException e) {
			LOGGER.error(FAIL_TO_SEND, e);
		} catch(Exception e) {
			LOGGER.error(FAIL_TO_SEND, e);
		}
		
	}
	/**
	 * creates temaplte for email
	 */
	@Async
	public void sendEmailFromTemplate(UserDTO userDto, String temlateName, String subject) {
		LOGGER.info("CREATE EMAIL FOR {}", userDto.toString());
		Locale locale =  Locale.ENGLISH;
		Context context  = new Context(locale);
		context.setVariable("user", userDto);
		context.setVariable("baseURL", BASE_URL);
		String content = templateEngine.process(temlateName, context);
		sendEmail(userDto.getEmail(), subject, content, false, true);
		
	}
	
	/**
	 * activation mail sending
	 */
	@Async
	public void sendActivationEmail(UserDTO user) {
		sendEmailFromTemplate(user, "mail/activation",  "LinkMe User Activation"); 
	}
	
	/**
	 * welcome mail sending
	 */
	@Async
	public void sendWelcomeEmail(UserDTO user) {
		sendEmailFromTemplate(user, "mail/welcome",  "Welcom new LinkMe User"); 
	}
	
	
}
