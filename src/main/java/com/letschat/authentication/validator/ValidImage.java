package com.letschat.authentication.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ImageFileConstraintValidator.class)
public @interface ValidImage {
	
	String message() default "Invalid image file";
	
	Class<?>[] groups() default {};
	
	Class<? extends Payload>[] payload() default {};
}
