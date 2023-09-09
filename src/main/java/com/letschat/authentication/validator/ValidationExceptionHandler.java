package com.letschat.authentication.validator;

import jakarta.servlet.http.HttpServletRequest;
import org.apache.tomcat.util.http.fileupload.impl.InvalidContentTypeException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class ValidationExceptionHandler {
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException e) {

		Map<String, String> errors = new LinkedHashMap<>();

		e.getBindingResult().getFieldErrors().forEach(error ->
			errors.put(error.getField(), error.getDefaultMessage())
		);

		return ResponseEntity
			.status(HttpStatus.UNPROCESSABLE_ENTITY)
			.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PROBLEM_JSON_VALUE)
			.body(errors);
	}
	
	@ExceptionHandler(InvalidContentTypeException.class)
	public ResponseEntity<Map<String, String>> handleInvalidContentException(InvalidContentTypeException e,
	                                                                         HttpServletRequest request) {
		Map<String, String> toReturn = new LinkedHashMap<>();
		
		toReturn.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
		toReturn.put("status", HttpStatus.BAD_REQUEST.name());
		toReturn.put("error", e.getClass().getSimpleName());
		toReturn.put("message", e.getMessage());
		toReturn.put("path", request.getRequestURI());
		
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(toReturn);
	}
}
