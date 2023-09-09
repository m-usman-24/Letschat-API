package com.letschat.authentication.bearer;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

@PropertySource("classpath:messages.properties")
@Service
public class JwtService {

	@Value("${jwt.secret.key}")
	private String secretKey;


	public String extractUserEmail(String token) {
		return extractClaim(token, Claims::getSubject);
	}
	
	private Key getSecretKey() {
		byte[] tokenBytes = Decoders.BASE64.decode(secretKey);
		return Keys.hmacShaKeyFor(tokenBytes);
	}
	
	public String generateToken(UserDetails userDetails) {
		return generateToken(Map.of(), userDetails);
	}

	public String generateToken(Map<String, Object> claims, UserDetails userDetails) {
		return
			Jwts.builder()
				.setClaims(claims)
				.setSubject(userDetails.getUsername())
				.setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24))
				.signWith(getSecretKey(), SignatureAlgorithm.HS256)
				.compact();
	}

	public boolean isTokenValid(String token, UserDetails userDetails) {
		String email = extractUserEmail(token);
		return email.matches(userDetails.getUsername()) && !isTokenExpired(token);
	}

	public boolean isTokenExpired(String token) {
		return extractExpiration(token).before(new Date());
	}

	private Date extractExpiration(String token) {
		return extractClaim(token, Claims::getExpiration);
	}

	private Claims extractClaims(String token) {

		return Jwts
			.parserBuilder()
			.setSigningKey(getSecretKey())
			.build()
			.parseClaimsJws(token)
			.getBody();
	}

	public <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
		return claimResolver.apply(extractClaims(token));
	}

}
