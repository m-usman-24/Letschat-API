package com.letschat.authentication.validator;

import com.letschat.user.UserRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UsernameConstraintValidator implements ConstraintValidator<ValidUsername, String> {
	
	private String regex;
	
	private final UserRepository userRepository;
	
	@Override
	public void initialize(ValidUsername constraintAnnotation) {
		regex = constraintAnnotation.regex();
	}
	
	@Override
	public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
		
		if (s != null && userRepository.existsById(s)) {
			
			constraintValidatorContext.disableDefaultConstraintViolation();
			
			constraintValidatorContext
				.buildConstraintViolationWithTemplate("Username is taken")
				.addConstraintViolation();
			
			return false;
		}
		
		return s == null || s.matches(regex);
	}
}
