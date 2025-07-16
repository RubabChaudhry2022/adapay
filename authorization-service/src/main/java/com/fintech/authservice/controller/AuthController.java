package com.fintech.authservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.fintech.authservice.dto.LoginRequest;
import com.fintech.authservice.dto.SignupRequest;
import com.fintech.authservice.dto.UserCreationRequest;
import com.fintech.authservice.service.AuthService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("v1/auth")
public class AuthController {

	@Autowired

	private AuthService authService;

	@PostMapping("/signup")
	public ResponseEntity<?> signup(@Valid @RequestBody SignupRequest request) {
		return authService.signup(request);
	}

	@PostMapping("/login")
	public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
		return authService.login(request);
	}

	@PreAuthorize("hasAuthority('ADMIN')")
	@GetMapping("/users")
	public ResponseEntity<?> users() {
		return authService.viewUsers();
	}

	@PreAuthorize("hasAuthority('ADMIN')")
	@PostMapping("/users")
	public ResponseEntity<?> createUsers(@Valid @RequestBody UserCreationRequest request,
			Authentication authentication) {

		String email = authentication.getName();
		return authService.createUsers(email, request);
	}

	@PostMapping("/refresh")
	public ResponseEntity<?> refreshToken(@RequestHeader("Authorization") String authHeader) {
		String refreshToken = authHeader.substring(7);
		System.out.println("Received Refresh Token: " + refreshToken);
		return authService.refreshAccessToken(refreshToken);

	}
	/*
	 * @GetMapping("/test") public ResponseEntity<String> testEndpoint() { return
	 * ResponseEntity.ok("Auth Service is working"); }
	 */
}