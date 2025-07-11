package com.fintech.authservice.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import com.fintech.authservice.model.Role;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {

    private final String secretKey = "a123FZxys0987LmnopQR456TuvWXz123"; // Should be at least 32 chars

    public SecretKey getKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    // Generate JWT with subject and role claim
    public String generateToken(String email, Role role) {
        return Jwts.builder()
                .subject(email)
                .claim("role", role.name())  
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 30)) // 30 minutes
                .signWith(getKey())
                .compact();
    }

    // Validate the token signature and expiration
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token);  // throws exception if invalid
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    // Extract email from token
    public String extractEmail(String token) {
        return Jwts.parser()
        		  .verifyWith(getKey())
                  .build()
                  .parseSignedClaims(token)
                  .getPayload()
                .getSubject();
    }

    // Extract role claim
    public String extractRole(String token) {
    	return  Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
    	.get("role", String.class);
		
    }
}
