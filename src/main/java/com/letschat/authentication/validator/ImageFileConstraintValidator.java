package com.letschat.authentication.validator;

import jakarta.validation.ConstraintValidator;

import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;


public class ImageFileConstraintValidator implements ConstraintValidator<ValidImage, MultipartFile> {
	
	@Override
	public void initialize(ValidImage constraintAnnotation) {
		// Not needed
	}
	
	@Override
	public boolean isValid(MultipartFile multipartFile, ConstraintValidatorContext context) {
		
		boolean result = true;
		String contentType = multipartFile.getContentType();
		
		if (!isSupportedContentType(Objects.requireNonNull(contentType))) {
			
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate("Only PNG or JPG images are allowed.")
				.addConstraintViolation();
			
			result = false;
		}
		
		return result;
	}
	
	private boolean isSupportedContentType(String contentType) {
		return contentType.equals("image/png")
			|| contentType.equals("image/jpg")
			|| contentType.equals("image/jpeg");
	}
}
