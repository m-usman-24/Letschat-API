package com.letschat.authentication.email;

import com.letschat.user.User;
import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Async;

public interface EmailSender {
	
	@Transactional
	String createEmailVerificationUrl(User user);
	
	@Async
	void sendEmail(String to, String receiverFullName, String subject, String url);
	
	String buildEmail(String receiverFullName, String url);
}
