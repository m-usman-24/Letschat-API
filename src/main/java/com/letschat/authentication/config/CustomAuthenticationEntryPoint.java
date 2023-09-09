package com.letschat.authentication.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
	@Override
	public void commence(HttpServletRequest request,
	                     HttpServletResponse response,
	                     AuthenticationException authException)
		throws IOException {
		
		if (authException instanceof BadCredentialsException) {
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Bad Credentials");
			
		} else if (authException instanceof LockedException ||
				   authException instanceof DisabledException) {
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Account Locked, Unverified Email");
			
		} else {
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized, Unknown Reasons");
		}
	}
}
