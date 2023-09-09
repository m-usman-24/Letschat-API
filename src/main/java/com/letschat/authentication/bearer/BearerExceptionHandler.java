package com.letschat.authentication.bearer;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class BearerExceptionHandler {
	
	@ExceptionHandler({ExpiredJwtException.class})
	public ResponseEntity<Map<String, String>> handleExpiredJwtException(RuntimeException ex,
	                                                                     HttpServletRequest request) {
		
		Map<String, String> toReturn = new LinkedHashMap<>();
		
			toReturn.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
			toReturn.put("status", HttpStatus.UNAUTHORIZED.name());
			toReturn.put("error", ex.getClass().getSimpleName());
			toReturn.put("message", ex.getMessage());
			toReturn.put("path", request.getRequestURI());
		
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(toReturn);
	}
}
