package com.letschat.chat.config;

import com.letschat.chat.exception.ImageCompressionException;
import com.letschat.chat.service.DevMessageSender;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ControllerAdvice;

import java.util.*;

@RequiredArgsConstructor
@ControllerAdvice
public class GlobalChatExceptionHandler {
	
	private final DevMessageSender devMessageSender;
	
	@MessageExceptionHandler
	@SendToUser(value = "queue/error", broadcast = false)
	public String handleConversationNotFoundException(ImageCompressionException e) {
		return e.getMessage();
	}
	
	@MessageExceptionHandler
	public void handleChatMessageValidationErrors(MethodArgumentNotValidException e,
	                                                             Message<?> message) {
		
		Map<String, String> errors = new LinkedHashMap<>();
		
		BindingResult bindingResult = Objects.requireNonNull(e.getBindingResult());
		bindingResult.getFieldErrors().forEach(err ->
			errors.put(err.getField(), err.getDefaultMessage()));
		
		devMessageSender.sendDevError(StompHeaderAccessor.wrap(message), errors.toString());
		
	}
}
