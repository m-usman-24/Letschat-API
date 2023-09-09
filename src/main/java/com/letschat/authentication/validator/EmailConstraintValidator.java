package com.letschat.authentication.validator;

import com.letschat.authentication.email.EmailSender;
import com.letschat.authentication.token.VerificationTokenService;
import com.letschat.user.User;
import com.letschat.user.UserRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EmailConstraintValidator implements ConstraintValidator<ValidEmail, String> {
	
	private String regex;
	
	private final UserRepository userRepository;
	private final VerificationTokenService verificationTokenService;
	private final EmailSender emailSender;
	
	@Override
	public void initialize(ValidEmail constraintAnnotation) {
		this.regex = constraintAnnotation.regex();
	}
	
	@Override
	public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
		
		if (s != null && userRepository.existsByEmailIgnoreCase(s)) {
			
			if (userRepository.existsUserByEmailWhereAccountLocked(s)) {
				
				constraintValidatorContext.disableDefaultConstraintViolation();
				constraintValidatorContext.buildConstraintViolationWithTemplate("Account with this email already " +
						"exists, verify your email, if the request was expired we've sent you a new one")
					.addConstraintViolation();
				
				if (verificationTokenService.isTokenExpired(s)) {
					
					User user = userRepository
						.findByEmailIgnoreCase(s)
						.orElseThrow(() -> new IllegalStateException("User not found with email while resending" +
							"confirmation email"));
					
					emailSender.sendEmail(s,
						user.getFirstName() + " " + user.getLastName(),
						"Verify Your Email",
						emailSender.createEmailVerificationUrl(user)
					);
				}
				
				return false;
			}
			
			constraintValidatorContext.disableDefaultConstraintViolation();
			
			constraintValidatorContext
				.buildConstraintViolationWithTemplate("Email already exists")
				.addConstraintViolation();
			
			return false;
		}
		
		return s == null || s.matches(regex);
	}
}
