package com.letschat.authentication.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = PasswordConstraintValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER, ElementType.FIELD, ElementType.METHOD})
public @interface ValidPassword {
	
	String regex() default "^(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\[\\]\\{\\}|\\\\:;\"'<>,.?/~`]).*$";
	
	String message() default "Password must contain characters a digit and a symbol";
	
	Class<?>[] groups() default {};
	
	Class<? extends Payload>[] payload() default {};
}
