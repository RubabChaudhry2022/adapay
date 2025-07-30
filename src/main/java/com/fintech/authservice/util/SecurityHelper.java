package com.fintech.authservice.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityHelper {

	private static Authentication getAuthentication() {
		return SecurityContextHolder.getContext().getAuthentication();
	}

	public static Long getCurrentUserId() {
		Object principal = getAuthentication().getPrincipal();
		return (principal instanceof Long) ? (Long) principal : null;
	}

	public static boolean isAdmin() {
		return getAuthentication().getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
	}
}
