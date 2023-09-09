package com.letschat.chat.config;

import com.letschat.chat.service.DevMessageSender;
import com.letschat.chat.service.OnConnectAuthenticationService;
import com.sun.security.auth.UserPrincipal;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class CustomChannelInterceptor implements ChannelInterceptor {
	
	private final OnConnectAuthenticationService authenticationService;
	private final DevMessageSender devMessageSender;
	
	
	@Override
	public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {
		
		StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
		
		var command = accessor != null ? accessor.getCommand() : null;
		
		if (command != null && command.equals(StompCommand.CONNECT)) {
			
			String  authHeader = accessor.getFirstNativeHeader("Authorization");
			
			if (authHeader == null) devMessageSender.sendDevError(accessor, "Missing Authorization Header (Bearer)");
			
			try {
				Authentication authenticatedUser = authenticationService.authenticateUser(authHeader);
				if (accessor.getUser() == null) accessor.setUser(new UserPrincipal(authenticatedUser.getName()));
				
			} catch (ExpiredJwtException | MalformedJwtException e) {
				log.warn(e.getMessage());
				devMessageSender.sendDevError(accessor, e.getMessage());
			}
		}
		
		return message;
	}
}
