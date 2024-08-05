package group9.events.service;

import freemarker.cache.ClassTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import group9.events.domain.entity.User;
import group9.events.repository.UserRepository;
import group9.events.service.interfaces.ConfirmationService;
import group9.events.service.interfaces.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.util.HashMap;
import java.util.Map;

@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender sender;
    private final Configuration mailConfig;
    private final ConfirmationService confirmationService;

    private final UserRepository userRepository;

    public EmailServiceImpl(JavaMailSender sender, Configuration mailConfig, ConfirmationService confirmationService, UserRepository userRepository) {
        this.sender = sender;
        this.mailConfig = mailConfig;
        this.confirmationService = confirmationService;
        this.userRepository = userRepository;


        mailConfig.setDefaultEncoding("UTF-8");
        mailConfig.setTemplateLoader(new ClassTemplateLoader(EmailServiceImpl.class, "/mail/"));
    }


    @Override
    public void sendConfirmationEmail(User user) {

        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");
        String emailText = generateEmailTextForRegistration(user);

        try {
            helper.setFrom("eventvoyagers@gmail.com");
            helper.setTo(user.getEmail());
            helper.setSubject("Registration");
            helper.setText(emailText, true);
            sender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendEmailWhenPasswordIsForgotten(String email) {
        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");
        String emailText = generateEmailTextForPasswordChanges(email);

        try {
            helper.setFrom("eventvoyagers@gmail.com");
            helper.setTo(email);
            helper.setSubject("Password Recovery");
            helper.setText(emailText, true);
            sender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    private String generateEmailTextForRegistration(User user) {
        try {
            Template template = mailConfig.getTemplate("confirm_reg_mail.ftlh");
            String code = confirmationService.generateConfirmationCode(user);
            String url = "http://localhost:8080/api/register?code=" + code;

            Map<String, Object> templateMap = new HashMap<>();
            templateMap.put("name", user.getFirstName());
            templateMap.put("link", url);

            return FreeMarkerTemplateUtils.processTemplateIntoString(template, templateMap);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String generateEmailTextForPasswordChanges(String email) {
        try {
            Template template = mailConfig.getTemplate("forgotten_password_mail.ftlh");
            User user = userRepository.findByEmail(email).orElseThrow(()->new UsernameNotFoundException("user don´t found"));
            String code = confirmationService.generateConfirmationCode(user);
            String url = "http://localhost:8080/api/auth/reset-password?code=" + code;

            Map<String, Object> templateMap = new HashMap<>();
            templateMap.put("email", email);
            templateMap.put("link", url);

            return FreeMarkerTemplateUtils.processTemplateIntoString(template, templateMap);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}