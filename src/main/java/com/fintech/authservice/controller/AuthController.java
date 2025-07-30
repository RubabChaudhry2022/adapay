package com.fintech.authservice.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.fintech.authservice.dto.AccessValidationResponse;
import com.fintech.authservice.dto.LoginRequest;
import com.fintech.authservice.dto.LogoutRequest;
import com.fintech.authservice.dto.PermissionCheckRequest;
import com.fintech.authservice.dto.SignupRequest;
import com.fintech.authservice.dto.TokenValidationResponse;
import com.fintech.authservice.service.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("v1/auth")
public class AuthController {

	private final AuthService authService;

	@PostMapping("/signup")
	public ResponseEntity<?> signup(@Valid @RequestBody SignupRequest request) {
		return authService.signup(request);
	}

	@PostMapping("/login")
	public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
		return authService.login(request);
	}

	@PostMapping("/refresh")
	public ResponseEntity<?> refreshToken(@RequestHeader("Authorization") String authHeader) {
		String refreshToken = authHeader.substring(7);
		return authService.refreshAccessToken(refreshToken);

	}

	@PostMapping("/validate-token")
	public ResponseEntity<TokenValidationResponse> validateToken() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		if (auth == null || !auth.isAuthenticated()) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new TokenValidationResponse(false, null, null));
		}

		String userId = auth.getName();
		String role = auth.getAuthorities().stream().findFirst()
				.map(grantedAuthority -> grantedAuthority.getAuthority().replace("ROLE_", "")).orElse(null);

		return ResponseEntity.ok(new TokenValidationResponse(true, Long.valueOf(userId), role));
	}

	@PostMapping("/validate-access")
	public ResponseEntity<?> validateAccess(@RequestHeader("Authorization") String authHeader,
			@RequestBody PermissionCheckRequest request) {

		String token = authHeader.substring(7);
		boolean isAllowed = authService.isAccessAllowed(token, request.getHttpMethod(), request.getUrl());

		return ResponseEntity.ok(new AccessValidationResponse(isAllowed));

	}

	@PostMapping("/logout")
	public ResponseEntity<?> logout(@RequestHeader("Authorization") String authHeader,
			@RequestBody LogoutRequest request) {
		String accessToken = authHeader.replace("Bearer ", "");
		String refreshToken = request.getRefreshToken();

		return authService.logout(accessToken, refreshToken);
	}
}