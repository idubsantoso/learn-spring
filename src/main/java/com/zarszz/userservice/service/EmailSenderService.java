package com.zarszz.userservice.service;

import com.zarszz.userservice.domain.SecretCode;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.*;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Random;

@Service
public class EmailSenderService {

    @Value("${spring.mailtrap.source}")
    private String emailSource;

    @Autowired
    JavaMailSender mailSender;

    @Autowired
    TemplateEngine emailTemplateEngine;

    @Autowired
    SecretCodeServiceImpl secretCodeService;

    public void sendMailWithInline(final String recipientName, final String recipientEmail) throws MessagingException {

        SecretCode secretCode = new SecretCode();
        secretCode.setCode(this.generateSecretCode());
        secretCode.setEmail(recipientEmail);

        this.secretCodeService.save(secretCode);

        // Prepare the evaluation context
        final Context ctx = new Context();
        ctx.setVariable("name", recipientName);
        ctx.setVariable("subscriptionDate", new Date());
        ctx.setVariable("secretCode", secretCode.getCode());

        // Prepare message using a Spring helper
        final MimeMessage mimeMessage = this.mailSender.createMimeMessage();
        final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, "UTF-8"); // true = multipart

        message.setSubject("Email kode rahasia anda");
        message.setFrom(emailSource);
        message.setTo(recipientEmail);

        // Create the HTML body using Thymeleaf
        final String htmlContent = this.emailTemplateEngine.process("secret-code.html", ctx);
        message.setText(htmlContent, true); // true = isHtml

        // Send mail
        this.mailSender.send(mimeMessage);
    }

    private String generateSecretCode() {
        Random rnd = new Random();
        return String.format("%06d", rnd.nextInt(999999));
    }
}
