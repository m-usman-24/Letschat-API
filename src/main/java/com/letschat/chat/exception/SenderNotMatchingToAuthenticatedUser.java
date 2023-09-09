package com.letschat.chat.exception;

public class SenderNotMatchingToAuthenticatedUser extends RuntimeException {
	
	public SenderNotMatchingToAuthenticatedUser(String message) {
		super(message);
	}
	
	public SenderNotMatchingToAuthenticatedUser(String message, Throwable cause) {
		super(message, cause);
	}
	
	public SenderNotMatchingToAuthenticatedUser(Throwable cause) {
		super(cause);
	}
	
	public SenderNotMatchingToAuthenticatedUser(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
