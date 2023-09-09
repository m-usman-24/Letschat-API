package com.letschat.chat.exception;

public class InvalidChatMessageDeletionAttemptException extends RuntimeException {
	
	public InvalidChatMessageDeletionAttemptException(String message) {
		super(message);
	}
	
	public InvalidChatMessageDeletionAttemptException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public InvalidChatMessageDeletionAttemptException(Throwable cause) {
		super(cause);
	}
	
	public InvalidChatMessageDeletionAttemptException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
