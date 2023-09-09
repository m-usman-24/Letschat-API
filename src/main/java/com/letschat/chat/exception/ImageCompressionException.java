package com.letschat.chat.exception;

public class ImageCompressionException extends RuntimeException {
	
	public ImageCompressionException(String message) {
		super(message);
	}
	
	public ImageCompressionException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public ImageCompressionException(Throwable cause) {
		super(cause);
	}
	
	public ImageCompressionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
