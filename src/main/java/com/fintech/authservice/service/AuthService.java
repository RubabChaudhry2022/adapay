package com.fintech.authservice.service;

import java.util.Date;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.fintech.authservice.dto.LoginRequest;
import com.fintech.authservice.dto.SignupRequest;
import com.fintech.authservice.dto.UserResponse;
import com.fintech.authservice.exception.InvalidCredentialsException;
import com.fintech.authservice.exception.ResourceNotFoundException;
import com.fintech.authservice.model.Role;
import com.fintech.authservice.model.User;
import com.fintech.authservice.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.time.Instant;

@RequiredArgsConstructor
@Slf4j
@Service
public class AuthService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;
	private final UserService userService;
	private final PermissionService permissionService;
	private final TokenBlacklistService tokenBlacklistService;

	public ResponseEntity<?> signup(SignupRequest request) {
		userService.validateEmailAndPhone(request.getEmail(), request.getPhoneNumber());

		User user = userService.buildAndSaveUser(request, request.getPassword(), Role.USER);
		UserResponse response = UserResponse.from(user);

		return ResponseEntity.ok(Map.of("message", "Account created successfully", "data", response));
	}

	public ResponseEntity<?> login(LoginRequest request) {
		log.info("Attempting login for email: {}", request.getEmail());

		User user = userRepository.findByEmail(request.getEmail())
				.orElseThrow(() -> new ResourceNotFoundException("User not found"));

		if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
			throw new InvalidCredentialsException("Credentials invalid");
		}

		String accessToken = jwtService.generateToken(user.getId(), user.getRole());
		String refreshToken = jwtService.generateRefreshToken(user.getId(), user.getRole());
		Date expiration = jwtService.extractExpiration(accessToken);

		return ResponseEntity.ok(Map.of("message", "Login successful", "accessToken", accessToken, "refreshToken",
				refreshToken, "expiresAt", expiration));
	}

	public ResponseEntity<?> refreshAccessToken(String refreshToken) {
		if (!jwtService.isRefreshToken(refreshToken)) {
			throw new AccessDeniedException("Invalid refresh token");
		}

		Long userId = jwtService.extractUserId(refreshToken);
		User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));

		String newAccessToken = jwtService.generateToken(user.getId(), user.getRole());
		return ResponseEntity.ok(Map.of("accessToken", newAccessToken));
	}

	public boolean isAccessAllowed(String token, String method, String path) {
		String role = jwtService.extractRole(token);
		System.out.println("Checking access for role=" + role + " to method=" + method + " path=" + path);
		return permissionService.isAllowed(method, path, role);
	}

	public ResponseEntity<?> logout(String accessToken, String refreshToken) {
		if (accessToken != null) {
			Instant accessExp = jwtService.extractExpiration(accessToken).toInstant();
			tokenBlacklistService.blacklist(accessToken, accessExp);
		}

		if (refreshToken != null) {
			Instant refreshExp = jwtService.extractExpiration(refreshToken).toInstant();
			tokenBlacklistService.blacklist(refreshToken, refreshExp);
		}

		return ResponseEntity.ok(Map.of("message", "Logout successful"));
	}

}
