package com.letschat.chat.dto;

import com.letschat.chat.entity.TypingState;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

/**
 * DTO for {@link com.letschat.chat.entity.Conversation}
 */
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConversationDto {
	private UUID id;
	private String username;
	private String fullName;
	private byte[] image;
	private Instant lastOnline;
	private TypingState typingState = TypingState.IDLE;
}
