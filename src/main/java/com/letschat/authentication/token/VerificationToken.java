package com.letschat.authentication.token;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.letschat.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity
@Table(name = "verification_token")
public class VerificationToken {
	@Id
	@GeneratedValue(
		strategy = GenerationType.SEQUENCE,
		generator = "verification_token_sequence_generator"
	)
	@SequenceGenerator(
		name = "verification_token_sequence_generator",
		sequenceName = "verification_token_sequence_generator",
		allocationSize = 1
	)
	@Column(name = "id", nullable = false)
	private Long id;
	
	@Column(name = "token")
	private String token;
	
	@Column(name = "created_at")
	private LocalDateTime createdAt;
	
	@Column(name = "expires_at")
	private LocalDateTime expiresAt;
	
	@Column(name = "verified_at")
	private LocalDateTime verifiedAt;
	
	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@JsonIgnore
	@ToString.Exclude
	private User user;
	
	public VerificationToken(String token,
	                         LocalDateTime createdAt,
	                         LocalDateTime expiresAt,
	                         User user) {
		this.token = token;
		this.createdAt = createdAt;
		this.expiresAt = expiresAt;
		this.user = user;
	}
}
