package com.fintech.authservice.service;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fintech.authservice.model.Role;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtService {

	@Value("${jwt.secret-key}")
	private String secretKey;

	@Value("${jwt.access-token.expiration}")
	private long accessTokenExpiration;

	@Value("${jwt.refresh-token.expiration}")
	private long refreshTokenExpiration;

	private SecretKey getKey() {
		return Keys.hmacShaKeyFor(secretKey.getBytes());
	}

	public String generateToken(Long id, Role role) {
		return Jwts.builder().subject(String.valueOf(id)).claim("role", role.name()).claim("type", "ACCESS")
				.issuedAt(new Date(System.currentTimeMillis()))
				.expiration(new Date(System.currentTimeMillis() + accessTokenExpiration)).signWith(getKey()).compact();
	}

	public String generateRefreshToken(Long id, Role role) {
		return Jwts.builder().subject(String.valueOf(id)).claim("role", role.name()).claim("type", "REFRESH")
				.issuedAt(new Date(System.currentTimeMillis()))
				.expiration(new Date(System.currentTimeMillis() + refreshTokenExpiration)).signWith(getKey()).compact();
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

	public boolean validateRefreshToken(String token) {
		return validateToken(token); // same logic
	}

	public boolean isAccessToken(String token) {
		return "ACCESS".equals(extractTokenType(token));
	}

	public boolean isRefreshToken(String token) {
		return "REFRESH".equals(extractTokenType(token));
	}

	private String extractTokenType(String token) {
		try {
			return extractAllClaims(token).get("type", String.class);
		} catch (Exception e) {
			return null;
		}
	}

	private Claims extractAllClaims(String token) {
		return Jwts.parser().verifyWith(getKey()).build().parseSignedClaims(token).getPayload();
	}

	public String extractRole(String token) {
		return extractAllClaims(token).get("role", String.class);
	}

	public long extractUserId(String token) {
		return Long.parseLong(extractAllClaims(token).getSubject());
	}

	public boolean isTokenExpired(String token) {
		return extractAllClaims(token).getExpiration().before(new Date());
	}

	public Date extractExpiration(String token) {
		return extractAllClaims(token).getExpiration();
	}
}
