package com.letschat.chat.exception;

public class InvalidBearerToken extends RuntimeException {
	
	public InvalidBearerToken(String message) {
		super(message);
	}
	
	public InvalidBearerToken(String message, Throwable cause) {
		super(message, cause);
	}
	
	public InvalidBearerToken(Throwable cause) {
		super(cause);
	}
	
	public InvalidBearerToken(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
