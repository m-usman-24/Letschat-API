package com.letschat.chat.config;

import com.letschat.chat.dto.UserStateDto;
import com.letschat.chat.entity.TypingState;
import com.letschat.chat.exception.UnauthorizedException;
import com.letschat.chat.mapper.ChatMappers;
import com.letschat.chat.repository.ConversationRepository;
import com.letschat.chat.service.ChatService;
import com.letschat.chat.service.DevMessageSender;
import com.letschat.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import java.security.Principal;
import java.time.Instant;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@Service
public class WebSocketStateListener {

	private final SimpMessagingTemplate messagingTemplate;
	private final UserRepository userRepository;
	private final ConversationRepository conversationRepository;
	private final ChatService chatService;
	private final DevMessageSender devMessageSender;
	
	
	@Transactional
	@EventListener
	public void handleState(SessionConnectEvent connectEvent) {
		
		Principal userPrincipal = connectEvent.getUser();
		
		String username = userPrincipal != null
						? userPrincipal.getName()
						: null;
		
		if (username == null) {
			devMessageSender.sendDevError(StompHeaderAccessor.wrap(connectEvent.getMessage()),
						            "User is not authenticated, could be a missing or invalid bearer");
			throw new UnauthorizedException("User is not authenticated, could be a missing or invalid bearer");
		}
		
		userRepository.setOnline(username, null, (byte) +1);

//		Only send the list of online users if 1st session is created if there are more we don't care
		
		if (userRepository.getSessions(username) == 1) propagateStateChange(username, null);
		
	}
	
	@Transactional
	@EventListener
	public void handleState(SessionDisconnectEvent disconnectEvent) {
		
		Principal userPrincipal = disconnectEvent.getUser();
		
		String username = userPrincipal != null
					    ? userPrincipal.getName()
					    : null;
		
		if (username == null) return;
		
//		Only send list of online users if there is only last session and it is also removed
		
		if (userRepository.getSessions(username) == 1) {
			userRepository.setOnline(username, Instant.now(), (byte) -1);
			propagateStateChange(username, Instant.now());
		} else {
			userRepository.setOnline(username, null, (byte) -1);
		}
		
	}
	
	@Transactional
	@EventListener
	public void handleState(SessionSubscribeEvent subscribeEvent) {
		
		StompHeaderAccessor accessor = StompHeaderAccessor.wrap(subscribeEvent.getMessage());
		
		String destination = accessor.getDestination();
		
		switch (Objects.requireNonNull(destination)) {
			
			case "/topic/online",
				 "/user/queue/error",
				 "/user/queue/chat",
				 "/user/queue/delete-message",
				 "/user/queue/conversations",
				 "/user/queue/previous-chat",
				 "/user/queue/change-user-state"
			-> devMessageSender.sendReceipt(accessor, "Subscribed to - " + accessor.getDestination());
			
			default -> {
				devMessageSender.sendDevError(accessor, "Unauthorized Subscribable Destination: " + destination);
				log.error("Unauthorized Subscription Destination: " + destination);
			}
		}
	}
	
	private void propagateStateChange(String username, Instant lastOnline) {
		
		UserStateDto userStateDto = UserStateDto.builder()
			.username(username)
			.lastOnline(lastOnline)
			.typingState(TypingState.IDLE)
			.build();
		
		conversationRepository
			.getAllConversations(username)
			.stream()
			.map(conversation -> ChatMappers.INSTANCE.toUsername(conversation, username))
			.forEach(u ->
				messagingTemplate.convertAndSendToUser(u, "/queue/change-user-state", userStateDto)
			);
		
		messagingTemplate.convertAndSend("/topic/online", chatService.fetchOnlineUsers());
		
	}
}
