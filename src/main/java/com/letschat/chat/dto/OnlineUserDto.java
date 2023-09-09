package com.letschat.chat.dto;

import lombok.*;

/**
 * DTO for {@link com.letschat.user.User}
 */
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OnlineUserDto {
	private String username;
	private String fullName;
	private byte[] profilePicture;
}
