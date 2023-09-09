package com.letschat.chat.service;

import com.letschat.authentication.bearer.JwtService;
import com.letschat.chat.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class OnConnectAuthenticationService {

	private final UserDetailsService userDetailsService;
	private final JwtService jwtService;
	
	public Authentication authenticateUser(String authHeader) {
		
		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			throw new UnauthorizedException("Incomplete or missing authentication headers");
		}
		
		var jwtToken = authHeader.substring(7);
		
		var username = jwtService.extractUserEmail(jwtToken);
		
		if (username != null || SecurityContextHolder.getContext().getAuthentication() == null) {
			
			UserDetails userDetails = userDetailsService.loadUserByUsername(username);
			
			if (jwtService.isTokenValid(jwtToken, userDetails)) {
				
				UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
					userDetails,
					null,
					userDetails.getAuthorities()
				);
				
				SecurityContextHolder.getContext().setAuthentication(authToken);
				
			}
		}
		
		return SecurityContextHolder.getContext().getAuthentication();
	}
}
