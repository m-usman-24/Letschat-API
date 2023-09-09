package com.letschat.chat.dto;

import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PreviousChatDto {
	private int currentPage;
	private UUID firstUnreadMessage;
	private long unreadMessageCount;
	private List<ChatMessageDto> chatMessages;
}
