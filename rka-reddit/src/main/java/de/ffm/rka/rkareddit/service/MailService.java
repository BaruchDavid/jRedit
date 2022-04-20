package de.ffm.rka.rkareddit.service;

import de.ffm.rka.rkareddit.domain.dto.UserDTO;
import de.ffm.rka.rkareddit.exception.ServiceException;
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

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;

/**
 * sending several email from template
 *
 * @author RKA
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
     * sends email async
     *
     * @throws ServiceException for application
     */
    //@Async
    public boolean sendEmail(String to, String subject, String content, boolean isHtml) throws ServiceException {
        LOGGER.info("TRY SEND EMAIL TO {} witch Subject {}", to, subject);
        try {
            LOGGER.info("SEND-EMAIL-THREAD-NAME {}", Thread.currentThread().getName());
            MimeMessage mimeMessage = this.mailSender.createMimeMessage();
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, "UTF-8");
            message.setTo(to);
            message.setFrom("noreplay@jReditt.com");
            message.setSubject(subject);
            message.setText(content, isHtml);
            mailSender.send(mimeMessage);
            LOGGER.info("SEND REGISTRATION MAIL SUCCESSFULLY TO {}", List.of(mimeMessage.getAllRecipients()));
            return true;
        } catch (MailException | MessagingException e) {
            LOGGER.error(FAIL_TO_SEND, "MailAuthenticationException", e);
            throw new ServiceException(FAIL_TO_SEND);
        }
    }

    /**
     * creates template for email
     */
    public boolean sendEmailFromTemplate(UserDTO userDto, String templateName, String subject) throws ServiceException{
        LOGGER.info("SEND-EMAIL-FROM TEMPLATE METHOD-CALL -THREAD-NAME {}", Thread.currentThread().getName());
        return sendEmail(userDto.getEmail(), subject, createEmailContext(baseUrl, userDto, templateName),  true);
    }

    private String createEmailContext(String baseUrl, UserDTO userDto, String templateName) {
        LOGGER.info("CREATE EMAIL FOR {}", userDto);
        Locale locale = Locale.ENGLISH;
        Context context = new Context(locale);
        context.setVariable("user", userDto);
        context.setVariable("baseURL", baseUrl);
        return templateEngine.process(templateName, context);
    }

    /**
     * activation mail sending
     */
    @Async
    public CompletableFuture<Boolean> sendActivationEmail(UserDTO user) throws ServiceException{
        LOGGER.info("Mail-Service Thread: {}", Thread.currentThread().getName());
        boolean linkMeUserActivation = sendEmailFromTemplate(user, "mail/activation", "LinkMe user Activation");
        return CompletableFuture.completedFuture(linkMeUserActivation);
    }

    @Async
    public void sendEmailToNewEmailAccount(UserDTO user) throws ServiceException{
        sendEmailFromTemplate(user, "mail/new_email_activation", "LinkMe user email change");
    }

    /**
     * welcome mail sending
     *
     * @throws ServiceException for application
     */
    @Async
    public void sendWelcomeEmail(UserDTO user) throws ServiceException{
        sendEmailFromTemplate(user, "mail/welcome", "Welcome new LinkMe User");
    }

    @Async
    public void sendRecoverEmail(UserDTO user) throws ServiceException{
        sendEmailFromTemplate(user, "mail/recoverPassword", "LinkMe password recovering");
    }


}
