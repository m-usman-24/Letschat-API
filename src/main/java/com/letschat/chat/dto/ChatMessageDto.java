package com.letschat.chat.dto;

import com.letschat.chat.entity.ChatMessageState;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

/**
 * DTO for {@link com.letschat.chat.entity.ChatMessage}
 */

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageDto implements Serializable {
	
	@NotNull
	private UUID id;
	private String content;
	private byte[] image;
	private String to;
	private String from;
	private String timeSent;
	private String timeReceived;
	private String timeRead;
	private List<ChatMessageState> messageStates;
	
}
