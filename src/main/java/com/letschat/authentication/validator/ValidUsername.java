package com.letschat.authentication.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = UsernameConstraintValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER, ElementType.FIELD, ElementType.METHOD})
public @interface ValidUsername {

	String regex() default "^[a-z0-9._]{1,30}$";
	
	String message() default "Only a-z, 0-9, ._ are allowed and max length of 30";
	
	Class<?>[] groups() default {};
	
	Class<? extends Payload>[] payload() default {};
}
