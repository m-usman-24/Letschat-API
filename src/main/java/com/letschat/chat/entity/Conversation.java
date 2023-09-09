package com.letschat.chat.entity;

import com.letschat.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(
	name = "conversation",
	uniqueConstraints = @UniqueConstraint(
		name = "unique_conversation_id",
		columnNames = "id"
	)
)
public class Conversation {
	
	@Id
	@Column(name = "id", nullable = false)
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;
	
	@ManyToOne
	@JoinColumn(name = "user_id_one")
	private User userOne;
	
	@ManyToOne
	@JoinColumn(name = "user_id_two")
	private User userTwo;
	
	public Conversation(User userOne, User userTwo) {
		this.userOne = userOne;
		this.userTwo = userTwo;
	}
}
