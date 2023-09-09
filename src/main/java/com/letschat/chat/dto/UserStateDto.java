package com.letschat.chat.dto;

import com.letschat.chat.entity.TypingState;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserStateDto {
	private String username;
	private Instant lastOnline;
	private TypingState typingState;
}
