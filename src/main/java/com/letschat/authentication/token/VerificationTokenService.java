package com.letschat.authentication.token;

import com.letschat.authentication.email.EmailSender;
import com.letschat.user.User;
import com.letschat.user.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class VerificationTokenService {
	
	private final VerificationTokenRepository verificationTokenRepository;
	private final UserService userService;
	private final EmailSender emailSender;
	
	@Transactional
	public String verifyToken(String token) {
		
		String verificationMessage = "Your email verification request is expired, a new email verification request " +
			"has been sent to your email address";
		
		VerificationToken verificationToken = verificationTokenRepository
			.findByToken(token)
			.orElseThrow(() -> new IllegalStateException("Token not found while verification"));
		
		if (verificationToken.getVerifiedAt() != null) {
			
			verificationMessage = "Your email is already verified";
			
		} else if (verificationToken.getExpiresAt().isAfter(LocalDateTime.now())) {
			
			verificationTokenRepository.setVerifiedAt(LocalDateTime.now(), verificationToken.getToken());
			userService.enableUser(verificationToken.getUser().getUsername());
			verificationMessage = "Your email is verified, and your account is enabled now";
			
		} else {
			
			User user = verificationToken.getUser();
			
			emailSender.sendEmail(user.getEmail(),
				user.getFirstName() + " " + user.getLastName(),
				"Verify Your Email",
				emailSender.createEmailVerificationUrl(user)
			);
			
		}
		
		return verificationMessage;
	}
	
	public boolean isTokenExpired(String email) {
		return verificationTokenRepository
			.findAllByUser_Email(email)
			.stream()
			.anyMatch(t -> t.getExpiresAt().isBefore(LocalDateTime.now()));
	}
}
