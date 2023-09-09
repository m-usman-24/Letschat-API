package com.letschat.chat.exception;

public class SameSenderAndReceiverException extends RuntimeException {
	
	public SameSenderAndReceiverException(String message) {
		super(message);
	}
	
	public SameSenderAndReceiverException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public SameSenderAndReceiverException(Throwable cause) {
		super(cause);
	}
	
	public SameSenderAndReceiverException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
