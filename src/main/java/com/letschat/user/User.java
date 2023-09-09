package com.letschat.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Fetch;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.util.Collection;
import java.util.Collections;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
	name = "user",
	uniqueConstraints = {
		@UniqueConstraint(
			name = "user_email_unique",
			columnNames = "email"
		),
		@UniqueConstraint(
			name = "user_username_unique",
			columnNames = "username"
		)
	}
)
public class User implements UserDetails {
	
	@Id
	@Column(name = "username")
	private String username;
	
	@Column(name = "first_name")
	private String firstName;
	
	@Column(name = "last_name")
	private String lastName;
	
	@Column(name = "email")
	private String email;
	
	@Column(name = "password")
	private String password;
	
	@Column(name = "last_online")
	private Instant lastOnline;
	
	@Column(name = "profile_picture", columnDefinition = "LONGBLOB")
	private byte[] profilePicture;
	
	@Column(name = "current-sessions")
	private Byte sessions = 0;
	
	@Column(name = "enabled")
	private boolean enabled = false;
	
	@Column(name = "account_locked")
	private boolean accountLocked = true;
	
	@Column(name = "account_expired")
	private boolean accountExpired = false;
	
	@Column(name = "credentials_expired")
	private boolean credentialsExpired = false;
	
	@JsonIgnore
	@Enumerated
	@Column(name = "role")
	@ToString.Exclude
	private Role role;
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return Collections.singletonList(new SimpleGrantedAuthority(role.name()));
	}
	
	@Override
	public boolean isAccountNonExpired() {
		return !accountExpired;
	}
	
	@Override
	public boolean isAccountNonLocked() {
		return !accountLocked;
	}
	
	@Override
	public boolean isCredentialsNonExpired() {
		return !credentialsExpired;
	}
}
