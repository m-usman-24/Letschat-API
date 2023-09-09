package com.letschat.chat.mapper;

import com.letschat.chat.dto.ChatMessageDto;
import com.letschat.chat.dto.ConversationDto;
import com.letschat.chat.dto.OnlineUserDto;
import com.letschat.chat.entity.ChatMessage;
import com.letschat.chat.entity.Conversation;
import com.letschat.chat.entity.TypingState;
import com.letschat.user.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ChatMappers {
	ChatMappers INSTANCE = Mappers.getMapper(ChatMappers.class);
	
	@Mapping(source = "to.username", target = "to")
	@Mapping(source = "from.username", target = "from")
	ChatMessageDto toDto(ChatMessage chatMessage);
	
	default ConversationDto toDto(Conversation conversation, String currentUser) {
		
		if (conversation.getUserOne().getUsername().equals(currentUser)) {
			return ConversationDto.builder()
				.id(conversation.getId())
				.username(conversation.getUserTwo().getUsername())
				.fullName(conversation.getUserTwo().getFirstName() + " " + conversation.getUserTwo().getLastName())
				.image(conversation.getUserTwo().getProfilePicture())
				.lastOnline(conversation.getUserTwo().getLastOnline())
				.typingState(TypingState.IDLE)
				.build();
		} else {
			return ConversationDto.builder()
				.id(conversation.getId())
				.username(conversation.getUserOne().getUsername())
				.fullName(conversation.getUserOne().getFirstName() + " " + conversation.getUserOne().getLastName())
				.image(conversation.getUserOne().getProfilePicture())
				.lastOnline(conversation.getUserOne().getLastOnline())
				.typingState(TypingState.IDLE)
				.build();
		}
	}
	
	default String toUsername(Conversation conversation, String username) {
		if (conversation.getUserOne().getUsername().equals(username)) {
			return conversation.getUserTwo().getUsername();
		} else {
			return conversation.getUserOne().getUsername();
		}
	}
	
	default OnlineUserDto toDto(User user) {
		return OnlineUserDto.builder()
			.username(user.getUsername())
			.fullName(user.getFirstName() + " " + user.getLastName())
			.profilePicture(user.getProfilePicture())
			.build();
	}
}
