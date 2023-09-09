package com.letschat.chat.service;

import com.letschat.chat.dto.*;
import com.letschat.chat.entity.ChatMessage;
import com.letschat.chat.entity.ChatMessageState;
import com.letschat.chat.entity.Conversation;
import com.letschat.chat.exception.*;
import com.letschat.chat.mapper.ChatMappers;
import com.letschat.chat.repository.ChatMessageRepository;
import com.letschat.chat.repository.ConversationRepository;
import com.letschat.user.User;
import com.letschat.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.Principal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.IntStream;

@RequiredArgsConstructor
@Slf4j
@Service
public class ChatService {
	
	private static final int MAX_PAGE_SIZE = 3;
	
	private final ConversationRepository conversationRepository;
	private final ChatMessageRepository chatMessageRepository;
	private final SimpMessagingTemplate simpMessagingTemplate;
	private final DevMessageSender devMessageSender;
	private final UserRepository userRepository;
	
	
	@SneakyThrows
	@Transactional
	public void submitMessage(ChatMessageDto message, StompHeaderAccessor accessor) {
		
		User[] users = message.getMessageStates() == null || message.getMessageStates().isEmpty()
					 ? fetchUsersByUsername(message.getTo(), message.getFrom())
					 : null;
		
		ChatMessageDto savedMessageToSend = saveOrUpdateMessage(users, message);
		
		if (savedMessageToSend.getTo().equals(savedMessageToSend.getFrom())) {
			devMessageSender.sendDevError(Objects.requireNonNull(accessor), "Field \"to\" & \"from\" are same");
			throw new SameSenderAndReceiverException("Field \"to\" & \"from\" are same");
		}
		
		boolean conversationExists = conversationRepository.existsConversation(savedMessageToSend.getTo(),
			savedMessageToSend.getFrom());
		
		if (!conversationExists) createAndSendConversation(Objects.requireNonNull(users), accessor);
		
		var userToSendMessageTo = savedMessageToSend.getTo();
		
		if (savedMessageToSend.getMessageStates() != null &&
			(savedMessageToSend.getMessageStates().contains(ChatMessageState.RECEIVED) ||
			savedMessageToSend.getMessageStates().contains(ChatMessageState.READ))) {
			userToSendMessageTo = savedMessageToSend.getFrom();
		}
		
		simpMessagingTemplate.convertAndSendToUser(userToSendMessageTo, "/queue/chat", savedMessageToSend);
		
	}
	
	public List<OnlineUserDto> fetchOnlineUsers() {
		
		List<User> onlineUsers = userRepository.getOnlineUsersExceptThis();
		
		return onlineUsers
		      .stream()
		      .map(ChatMappers.INSTANCE::toDto)
		      .toList();
	}
	
	public List<ConversationDto> fetchConversations(String name) {
		
		List<Conversation> conversations = conversationRepository.getAllConversations(name);
		
		return conversations
			  .stream()
			  .map(c -> ChatMappers.INSTANCE.toDto(c, name))
			  .toList();
		
	}
	
	public PreviousChatDto fetchPreviousChat(UUID conversationId, int page, String currentUser) {
		
		Conversation conversation = conversationRepository
			.findById(conversationId)
					.orElseThrow(() -> new ConversationNotFoundException("Conversation not found while fetching " +
																		 "previous chat"));
		
		var userOneUsername = conversation.getUserOne().getUsername();
		var userTwoUsername = conversation.getUserTwo().getUsername();
		
		int currentPage = page;
		
		List<ChatMessage> chatMessages = new ArrayList<>(chatMessageRepository.findByConversation(
			userOneUsername,
			userTwoUsername,
			PageRequest.of(page, MAX_PAGE_SIZE))
		);
		
		if (page == 0) {
			
			for (int previousSize = 0;
			     (chatMessages.get(chatMessages.size() - 1).getTimeRead() == null) &&
			     (!chatMessages.get(chatMessages.size() - 1).getFrom().getUsername().equals(currentUser));
				 currentPage++, previousSize = chatMessages.size()) {
				
				chatMessages.addAll(chatMessageRepository.findByConversation(
					userOneUsername,
					userTwoUsername,
					PageRequest.of(currentPage + 1, MAX_PAGE_SIZE)
				));
				
				if (previousSize == chatMessages.size()) break;
				
			}
		}
		
		long unreadMessageCount = chatMessages
			.stream()
			.filter(m -> !m.getFrom().getUsername().equals(currentUser) && m.getTimeRead() == null)
			.count();
		
		UUID firstUnreadMessage = IntStream.range(0, chatMessages.size())
			.mapToObj(i -> chatMessages.get(chatMessages.size() - 1 - i))
			.filter(m -> !m.getFrom().getUsername().equals(currentUser) && m.getTimeRead() == null)
			.map(ChatMessage::getId)
			.findFirst()
			.orElse(null);
		
		List<ChatMessageDto> chatMessageDtoList = chatMessages
			  .stream()
			  .map(ChatMappers.INSTANCE::toDto)
			  .toList();
		
		return new PreviousChatDto(currentPage, firstUnreadMessage, unreadMessageCount, chatMessageDtoList);
		
	}
	
	private User[] fetchUsersByUsername(String userOneUsername, String userTwoUsername) {
		
		User userOne = userRepository
			.findById(userOneUsername)
			.orElseThrow(() -> new UsernameNotFoundException("User not found when creating conversation"));
		
		User userTwo = userRepository
			.findById(userTwoUsername)
			.orElseThrow(() -> new UsernameNotFoundException("User not found when creating conversation"));
		
		return new User[] {userOne, userTwo};
		
	}
	
	private byte[] tryImageCompression(byte[] imageBytes) throws ImageCompressionException {
		try {
			return ImageCompressionService.compressImage(imageBytes, 1.75f);
		} catch (IOException e) {
			throw new ImageCompressionException("Image is not sent due to compression error, try another image");
		}
	}
	
	private void createAndSendConversation(User[] users, StompHeaderAccessor accessor) {
		
		Conversation newConversation = conversationRepository.save(new Conversation(users[0], users[1]));
		
		Principal senderPrincipal = Objects.requireNonNull(accessor.getUser());
		String senderUsername = Objects.requireNonNull(senderPrincipal.getName());
		// User to send conversation to
		String receiverUsername = senderUsername.equals(users[0].getUsername())
								? users[1].getUsername()
								: users[0].getUsername();
		
		simpMessagingTemplate.convertAndSendToUser(receiverUsername,
										          "/queue/conversations",
											       ChatMappers.INSTANCE.toDto(newConversation, receiverUsername));
		
		simpMessagingTemplate.convertAndSendToUser(senderUsername,
										          "/queue/conversations",
												  ChatMappers.INSTANCE.toDto(newConversation, senderUsername));
		
	}
	
	public void changeUserState(UserStateDto userStateDto, String username) {
		
		conversationRepository
			.getAllConversations(username)
			.stream()
			.map(conversation -> ChatMappers.INSTANCE.toUsername(conversation, username))
			.forEach(u -> {
				if (userStateDto.getUsername() == null) userStateDto.setUsername(username);
				simpMessagingTemplate.convertAndSendToUser(u, "/queue/change-user-state", userStateDto);
			});
		
	}
	
	ChatMessageDto saveOrUpdateMessage(User[] users, ChatMessageDto message) {
			
		if (message.getMessageStates() == null || message.getMessageStates().isEmpty()) {
			
			byte[] imageToSave = message.getImage() != null
				               ? tryImageCompression(message.getImage())
				               : null;
			
			ChatMessage chatMessageToSave = ChatMessage.builder()
				.id(message.getId())
				.content(message.getContent())
				.image(imageToSave)
				.to(users[0])
				.from(users[1])
				.timeSent(Instant.now())
				.messageStates(null)
				.build();
			
			chatMessageRepository.save(chatMessageToSave);
			
			return ChatMappers.INSTANCE.toDto(chatMessageToSave);
		}
		
		ChatMessage chatMessage  = chatMessageRepository
			.findById(message.getId())
			.orElseThrow(() -> new ChatMessageNotFoundException("Chat message not found while saving or updating"));
		
		chatMessage.setMessageStates(message.getMessageStates());
		
		if (chatMessage.getMessageStates().contains(ChatMessageState.DELETED)) {
			chatMessage.setTimeReceived(null);
			chatMessage.setTimeRead(null);
			chatMessage.setContent(null);
			chatMessage.setImage(null);
		}
		
		if (chatMessage.getMessageStates().contains(ChatMessageState.EDITED)) {
			chatMessage.setTimeReceived(null);
			chatMessage.setTimeRead(null);
			chatMessage.setContent(message.getContent());
		}
		
		if (message.getMessageStates().contains(ChatMessageState.RECEIVED)) {
			chatMessage.setTimeReceived(Instant.now());
		}
		
		if (message.getMessageStates().contains(ChatMessageState.READ)) {
			chatMessage.setTimeRead(Instant.now());
		}
		
		chatMessageRepository.save(chatMessage);
		
		return ChatMappers.INSTANCE.toDto(chatMessage);
	}
}
