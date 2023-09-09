package com.letschat.chat.exception;

public class ConversationNotFoundException extends RuntimeException {
	
	public ConversationNotFoundException(String message) {
		super(message);
	}
	
	public ConversationNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public ConversationNotFoundException(Throwable cause) {
		super(cause);
	}
	
	public ConversationNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
