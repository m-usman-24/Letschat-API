package com.letschat.authentication.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordConstraintValidator implements ConstraintValidator<ValidPassword, String> {
	
	private String regex;
	
	@Override
	public void initialize(ValidPassword constraintAnnotation) {
		this.regex = constraintAnnotation.regex();
	}
	
	@Override
	public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
		return s == null || s.matches(regex);
	}
}
