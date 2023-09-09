package com.letschat.chat.entity;

import com.letschat.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "chat_message")
public class ChatMessage {
	
	@Id
	@Column(name = "id", nullable = false, unique = true)
	private UUID id;
	
	@Column(name = "content", columnDefinition = "text")
	private String content;
	
	@Column(name = "image", columnDefinition = "LONGBLOB")
	private byte[] image;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id_to", updatable = false)
	private User to;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id_from", updatable = false)
	private User from;
	
	@Column(name = "time_sent")
	private Instant timeSent;
	
	@Column(name = "time_received")
	private Instant timeReceived;
	
	@Column(name = "time_read")
	private Instant timeRead;
	
	@Enumerated
	@Column(name = "message_states")
	private List<ChatMessageState> messageStates;
}
