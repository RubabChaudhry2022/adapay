package com.fintech.authservice.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import com.fintech.authservice.model.Role;

import javax.crypto.SecretKey;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class JwtService {

	private final String secretKey = "a123FZxys0987LmnopQR456TuvWXz123";

	public SecretKey getKey() {
		return Keys.hmacShaKeyFor(secretKey.getBytes());
	}

	public String generateToken(String email, Long id, Role role) {
		return Jwts.builder().subject(email).claim("id", id).claim("role", role.name()).claim("type", "ACCESS")
				.issuedAt(new Date(System.currentTimeMillis()))
				.expiration(new Date(System.currentTimeMillis() + 10 * 60 * 1000)) // 10 mins
				.signWith(getKey()).compact();
	}

	public boolean validateToken(String token) {
		try {
			Jwts.parser().verifyWith(getKey()).build().parseSignedClaims(token);
			return true;
		} catch (JwtException e) {
			log.error("Token validation failed: {}", e.getMessage());
			return false;
		}
	}

	public String generateRefreshToken(String email, Long id, Role role) {
		return Jwts.builder().subject(email).claim("id", id).claim("role", role.name()).claim("type", "REFRESH")
				.issuedAt(new Date(System.currentTimeMillis()))
				.expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 7)) // 7 days
				.signWith(getKey()).compact();
	}

	public boolean validateRefreshToken(String token) {
		try {
			Jwts.parser().verifyWith(getKey()).build().parseSignedClaims(token);
			return true;
		} catch (JwtException e) {
			log.error("Token validation failed: {}", e.getMessage());
			return false;
		}
	}

	private String extractTokenType(String token) {
		try {
			return extractAllClaims(token).get("type", String.class);
		} catch (Exception e) {
			return null;
		}
	}

	public boolean isAccessToken(String token) {
		return "ACCESS".equals(extractTokenType(token));
	}

	public boolean isRefreshToken(String token) {
		return "REFRESH".equals(extractTokenType(token));
	}

	private Claims extractAllClaims(String token) {
		return Jwts.parser().verifyWith(getKey()).build().parseSignedClaims(token).getPayload();
	}

	public String extractEmail(String token) {
		return extractAllClaims(token).getSubject();
	}

	public String extractRole(String token) {
		return extractAllClaims(token).get("role", String.class);

	}

	public String extractUserId(String token) {
		return extractAllClaims(token).get("id", String.class);
	}

	public boolean isTokenExpired(String token) {
		return extractAllClaims(token).getExpiration().before(new Date());
	}

	public Date extractExpiration(String token) {
		return extractAllClaims(token).getExpiration();
	}
	

}
