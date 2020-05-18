package de.ffm.rka.rkareddit.service;

import java.util.List;
import java.util.Locale;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;
import de.ffm.rka.rkareddit.domain.dto.UserDTO;

/**
 * sending several email from template
 * @author RKA
 *
 */
@Service
public class MailService {
	private static final String FAIL_TO_SEND = "FAIL TO SEND EMAIL {}";
	private static final Logger LOGGER = LoggerFactory.getLogger(MailService.class);
	private final JavaMailSender mailSender;
	private final SpringTemplateEngine templateEngine;
	

	@Value("${linkMeEmailService}")
	private String baseUrl;
	
	public MailService(JavaMailSender mailSender, SpringTemplateEngine templateEngine) {
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
			LOGGER.info("SEND REGISTRATION MAIL SUCCESSFULLY TO {}", List.of(mimeMessage.getAllRecipients()));
		} catch (MailAuthenticationException  e) {
			LOGGER.error(FAIL_TO_SEND , "MailAuthenticationException", e);
		} catch (MailSendException e) {
			LOGGER.error(FAIL_TO_SEND, "MailSendException", e);
		} catch (MailException e) {
			LOGGER.error(FAIL_TO_SEND, "MailException", e);
		} catch (MessagingException e) {
			LOGGER.error(FAIL_TO_SEND, "MessagingException", e);
		}
	}
	/**
	 * creates temaplte for email
	 */
	@Async
	public void sendEmailFromTemplate(UserDTO userDto, String temlateName, String subject) {
		LOGGER.info("CREATE EMAIL FOR {}", userDto);
		Locale locale =  Locale.ENGLISH;
		Context context  = new Context(locale);
		context.setVariable("user", userDto);
		context.setVariable("baseURL", baseUrl);
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
