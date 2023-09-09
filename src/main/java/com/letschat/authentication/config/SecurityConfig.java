package com.letschat.authentication.config;

import com.letschat.authentication.bearer.ExpiredJwtExceptionHandlerFilter;
import com.letschat.authentication.bearer.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig {
	
	private final AuthenticationProvider authenticationProvider;
	private final JwtAuthenticationFilter jwtAuthFilter;
	private final ExpiredJwtExceptionHandlerFilter exceptionHandlerFilter;
	
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		
		return http
			.authorizeHttpRequests(customizer ->
				customizer
					.requestMatchers(
						r -> r.getHeader("Upgrade") != null &&
							r.getHeader("Upgrade").equalsIgnoreCase("websocket")
					)
					.permitAll()
					.requestMatchers("/swagger-ui/**", "/v3/api-docs/**")
					.permitAll()
					.requestMatchers("/error")
					.permitAll()
					.requestMatchers("/letschat/onboard", "/letschat/login", "/letschat/verify")
					.permitAll()
					.anyRequest()
					.authenticated()
			)
			.csrf(AbstractHttpConfigurer::disable)
			.authenticationProvider(authenticationProvider)
			.sessionManagement(customizer ->
				customizer.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			)
			.addFilterBefore(exceptionHandlerFilter, UsernamePasswordAuthenticationFilter.class)
			.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
			.build();
	}
}
