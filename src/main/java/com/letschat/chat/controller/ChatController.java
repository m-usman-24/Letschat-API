package com.letschat.chat.controller;

import com.letschat.chat.dto.ChatMessageDto;
import com.letschat.chat.dto.ConversationDto;
import com.letschat.chat.dto.PreviousChatDto;
import com.letschat.chat.dto.UserStateDto;
import com.letschat.chat.service.ChatService;
import com.letschat.chat.service.DevMessageSender;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
public class ChatController {
	
	private final SimpMessagingTemplate messagingTemplate;
	private final ChatService chatService;
	private final DevMessageSender devMessageSender;
	
	@MessageMapping("/chat")
	public void send(@Payload @Valid ChatMessageDto payload, Message<?> message) {
		
		StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
		chatService.submitMessage(payload, accessor);

		devMessageSender.sendReceipt(accessor, "Processed by Server");
	}
	
	@MessageMapping("/conversations")
	@SendToUser(broadcast = false)
	public List<ConversationDto> sendConversations(Principal principal) {
		return chatService.fetchConversations(principal.getName());
	}
	
	@MessageMapping("/previous-chat/{conversationId}/{page}")
	@SendToUser(value = "/queue/previous-chat", broadcast = false)
	public PreviousChatDto sendPreviousChat(@DestinationVariable("conversationId") UUID conversationId,
	                                        @DestinationVariable("page") int page,
	                                        Principal userPrincipal) {
		return chatService.fetchPreviousChat(conversationId, page, userPrincipal.getName());
	}
	
	@MessageMapping("/online")
	public void onlineUsers(Message<?> message) {
		messagingTemplate.convertAndSend("/topic/online", chatService.fetchOnlineUsers());
		devMessageSender.sendReceipt(StompHeaderAccessor.wrap(message), "Processed by Server");
	}
	
	@MessageMapping("/change-user-state")
	public void changeUserState(@Payload UserStateDto userStateDto, Principal userPrincipal) {
		chatService.changeUserState(userStateDto, userPrincipal.getName());
	}
}
