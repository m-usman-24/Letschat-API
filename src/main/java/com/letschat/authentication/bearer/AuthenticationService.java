package com.letschat.authentication.bearer;

import com.letschat.authentication.dto.AuthenticationRequest;
import com.letschat.authentication.dto.AuthenticationResponse;
import com.letschat.user.User;
import com.letschat.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AuthenticationService {
	
	private final AuthenticationManager authenticationManager;
	private final UserRepository userRepository;
	private final JwtService jwtService;
	
	public AuthenticationResponse authenticate(AuthenticationRequest request) {
		
		authenticationManager
			.authenticate(new UsernamePasswordAuthenticationToken(request.username(), request.password()));
		
		User user = userRepository
			.findById(request.username())
			.orElseThrow(() -> new UsernameNotFoundException("User not found while authenticating"));
		
		var token = jwtService.generateToken(user);
		
		return new AuthenticationResponse(token,
										  user.getUsername(),
										  user.getFirstName() + " " + user.getLastName());
	}
}
