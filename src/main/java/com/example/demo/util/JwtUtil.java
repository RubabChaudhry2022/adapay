package com.example.demo.util;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import com.example.demo.dto.UserDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureException;

@Component
public class JwtUtil {

	private final String secretKey = "a123FZxys0987LmnopQR456TuvWXz123";

	public UserDto extractUser(String token) {
		try {
			Claims claims = Jwts.parser().setSigningKey(secretKey.getBytes()).parseClaimsJws(token).getBody();

			UserDto user = new UserDto();
			user.setId(Long.valueOf(claims.getSubject()));
			user.setEmail(claims.getSubject());
			user.setRole(claims.get("role").toString());

			return user;

		} catch (SignatureException e) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token signature");
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or expired token");
		}

	}

	public Long extractUserIdFromToken(String token) {
		token = token.replace("Bearer ", "");
		Claims claims = Jwts.parser().setSigningKey(secretKey.getBytes()).parseClaimsJws(token).getBody();

		return Long.parseLong(claims.getSubject());
	}
	public String extractRoleFromToken(String token) {
		try {
			Claims claims = Jwts.parser()
				.setSigningKey(secretKey.getBytes())
				.parseClaimsJws(token.replace("Bearer ", ""))
				.getBody();
			return claims.get("role", String.class);
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or expired token");
		}
	}


}