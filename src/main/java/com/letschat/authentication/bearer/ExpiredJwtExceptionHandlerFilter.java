package com.letschat.authentication.bearer;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

@Component
public class ExpiredJwtExceptionHandlerFilter extends OncePerRequestFilter {
	
	private final HandlerExceptionResolver exceptionResolver;
	
	public ExpiredJwtExceptionHandlerFilter(
		@Qualifier("handlerExceptionResolver") HandlerExceptionResolver exceptionResolver) {
		this.exceptionResolver = exceptionResolver;
	}
	
	@Override
	protected void doFilterInternal(
		@NonNull HttpServletRequest request,
		@NonNull HttpServletResponse response,
		@NonNull FilterChain filterChain)
		throws ServletException, IOException {
		
		try {
			filterChain.doFilter(request, response);
			
		} catch (ExpiredJwtException e) {
			if (exceptionResolver.resolveException(request, response, null, e) == null) throw e;
		}
	}
}
