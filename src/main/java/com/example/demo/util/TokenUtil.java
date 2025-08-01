package com.example.demo.util;

import jakarta.servlet.http.HttpServletRequest;

public class TokenUtil {

	public static boolean isValidToken(String token) {
		return token != null && token.startsWith("Bearer ");
	}

	public static String extractJwt(String token) {
		if (!isValidToken(token)) {
			throw new IllegalArgumentException("Invalid token format");
		}
		return token.substring(7);
	}

	public static String getBearerToken(HttpServletRequest request) {
		String authHeader = request.getHeader("Authorization");
		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			return authHeader.substring(7);
		}
		return null;
	}
}
