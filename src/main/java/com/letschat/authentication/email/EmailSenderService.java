package com.letschat.authentication.email;

import com.letschat.authentication.token.VerificationToken;
import com.letschat.authentication.token.VerificationTokenRepository;
import com.letschat.user.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@RequiredArgsConstructor
@PropertySource("classpath:messages.properties")
@Service
@Slf4j
public class EmailSenderService implements EmailSender {
	
	private final JavaMailSender javaMailSender;
	private final TemplateEngine templateEngine;
	private final VerificationTokenRepository verificationTokenRepository;
	private final HttpServletRequest request;
	
	@Value("${email.sender}")
	private String from;
	
	@Override
	public String createEmailVerificationUrl(User user) {
	
		String token = UUID.randomUUID().toString();
		
		VerificationToken verificationToken = new VerificationToken(
			token,
			LocalDateTime.now(),
			LocalDateTime.now().plusMinutes(15),
			user
		);
		
		verificationTokenRepository.save(verificationToken);
		
		return String.valueOf(request.getRequestURL()).replace(request.getRequestURI(), "")
			+ "/letschat/verify?token=" + token;
	}
	
	@Override
	public void sendEmail(String to, String receiverFullName, String subject, String url) {
		
		MimeMessage mimeMessage = javaMailSender.createMimeMessage();
		MimeMessageHelper message = new MimeMessageHelper(mimeMessage);
		
		try {
			message.setTo(to);
			message.setFrom(from);
			message.setText(buildEmail(receiverFullName, url), true);
			message.setSubject(subject);
			message.setSentDate(new Date());
			javaMailSender.send(mimeMessage);
			
		} catch (MessagingException e) {
			log.error("Unable to send email to " + receiverFullName);
			throw new RuntimeException("Unable to send email to " + receiverFullName);
		}
	}
	
	@Override
	public String buildEmail(String receiverFullName, String url) {
		
		Context context = new Context();
		
		context.setVariable("receiverFullName", receiverFullName);
		context.setVariable("url", url);
		
		return templateEngine.process("emailTemplate.html", context);
	}
}
