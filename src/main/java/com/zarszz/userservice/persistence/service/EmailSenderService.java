package com.zarszz.userservice.persistence.service;

import com.zarszz.userservice.domain.Payment;
import com.zarszz.userservice.domain.SecretCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.*;
import javax.mail.internet.MimeMessage;
import java.util.Calendar;
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

		sendMail(ctx, "Email kode rahasia anda", "secret-code.html", recipientEmail);
	}

	public void sendPaymentEmail(final String recipientName, final String recipientEmail, final Payment payment) throws MessagingException {
		final Context ctx = new Context();
		ctx.setVariable("name", recipientName);
		ctx.setVariable("total", payment.getTotal());
		ctx.setVariable("paymentLink", payment.getRedirectUrl());
		ctx.setVariable("subscriptionDate", new Date());

		sendMail(ctx, "Mohon lanjutkan pembayaran anda", "process-payment.html", recipientEmail);
	}

	public void sendTransactionStatusEmail(final String recipientEmail, final String state, final Payment payment) throws MessagingException {
		final Context ctx = new Context();
		ctx.setVariable("name", payment.getUser().getName());
		ctx.setVariable("total", payment.getTotal());
		ctx.setVariable("state", state);
		ctx.setVariable("paymentCode", payment.getPaymentCode());

		sendMail(ctx, "Pemberitahuan transaksi", "transaction-status.html", recipientEmail);
	}

	public void sendInformationUserToPayment(final Payment payment) throws MessagingException {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(payment.getCreatedAt());
		calendar.add(Calendar.DAY_OF_MONTH, 3);

		final Context ctx = new Context();
		ctx.setVariable("name", payment.getUser().getName());
		ctx.setVariable("total", payment.getTotal());
		ctx.setVariable("subscriptionDate", payment.getCreatedAt());
		ctx.setVariable("paymentDeadline", calendar.getTime());
		ctx.setVariable("paymentLink", payment.getRedirectUrl());

		sendMail(ctx, "Mohon lanjutkan pembayaran anda", "send-user-do-payment.html", payment.getUser().getEmail());
	}

	public void sendInformationPaymentExpired(final Payment payment) throws MessagingException {
		final Context ctx = new Context();
		ctx.setVariable("subscriptionDate", payment.getCreatedAt());
		ctx.setVariable("trxCode", payment.getPaymentCode());

		sendMail(ctx, payment.getUser().getEmail(), "Pemberitahuan transaksi expired", "send-user-payment-expired.html");
	}

	private void sendMail(Context context, String subject, String template, String recipientEmail) throws MessagingException {
		// Prepare message using a Spring helper
		final MimeMessage mimeMessage = this.mailSender.createMimeMessage();
		final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, "UTF-8");

		message.setSubject(subject);
		message.setFrom(emailSource);
		message.setTo(recipientEmail);

		// Create the HTML body using Thymeleaf
		final String htmlContent = this.emailTemplateEngine.process(template, context);
		message.setText(htmlContent, true);

		// Send mail
		this.mailSender.send(mimeMessage);
	}

	private String generateSecretCode() {
		Random rnd = new Random();
		return String.format("%06d", rnd.nextInt(999999));
	}
}
