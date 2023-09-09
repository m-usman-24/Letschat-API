package com.letschat.chat.exception;

public class ChatMessageNotFoundException extends RuntimeException {
	
	public ChatMessageNotFoundException(String message) {
		super(message);
	}
	
	public ChatMessageNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public ChatMessageNotFoundException(Throwable cause) {
		super(cause);
	}
	
	public ChatMessageNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
