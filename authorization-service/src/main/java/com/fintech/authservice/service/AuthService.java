package com.fintech.authservice.service;

import com.fintech.authservice.dto.LoginRequest;
import com.fintech.authservice.dto.SignupRequest;
import com.fintech.authservice.dto.UserCreationRequest;
import com.fintech.authservice.dto.UserResponse;
import com.fintech.authservice.exception.ResourceAlreadyExistsException;
import com.fintech.authservice.exception.ResourceNotFoundException;
import com.fintech.authservice.model.Role;
import com.fintech.authservice.model.User;
import com.fintech.authservice.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;

	public ResponseEntity<?> signup(SignupRequest request) {
		if (userRepository.findByEmail(request.getEmail()).isPresent()) {
			throw new ResourceAlreadyExistsException("Email already exists");
		}

		User user = new User();
		user.setFirstName(request.getFirstName());
		user.setLastName(request.getLastName());
		user.setPhoneNumber(request.getPhoneNumber());
		user.setEmail(request.getEmail());
		user.setPassword(passwordEncoder.encode(request.getPassword()));

		Role assignedRole = request.getEmail().endsWith("@vaultspay.com") ? Role.ADMIN : Role.USER;
		user.setRole(assignedRole);

		userRepository.save(user);

		String fullName = user.getFirstName() + " " + user.getLastName();

		return ResponseEntity.ok(Map.of("message", "Account created successfully", "user",
				new UserResponse(fullName, user.getEmail(), user.getPhoneNumber())));
	}

	public ResponseEntity<?> login(LoginRequest request) {
		User user = userRepository.findByEmail(request.getEmail())
				.orElseThrow(() -> new ResourceNotFoundException("User not found"));

		if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
			throw new AccessDeniedException("Invalid credentials");
		}

		String accessToken = jwtService.generateToken(user.getEmail(), user.getId(), user.getRole());
		String refreshToken = jwtService.generateRefreshToken(user.getEmail(), user.getId(), user.getRole());
		Date expiration = jwtService.extractExpiration(accessToken);

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		return ResponseEntity.ok(Map.of("message", "Login successful", "accessToken", accessToken, "refreshToken",
				refreshToken, "expiresAt", sdf.format(expiration)));
	}

	private User getAuthenticatedUser() {
		String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		return userRepository.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found"));
	}

	public ResponseEntity<?> viewUsers() {
		User currentUser = getAuthenticatedUser();
		if (currentUser.getRole() != Role.ADMIN) {
			throw new AccessDeniedException("Access denied");
		}

		List<UserResponse> users = userRepository.findAll().stream()
				.map(u -> new UserResponse(u.getFirstName() + " " + u.getLastName(), u.getEmail(), u.getPhoneNumber()))
				.toList();

		return ResponseEntity.ok(users);
	}

	public ResponseEntity<?> createUsers(String adminEmail, UserCreationRequest request) {
		User currentUser = userRepository.findByEmail(adminEmail)
				.orElseThrow(() -> new ResourceNotFoundException("Admin user not found"));

		if (currentUser.getRole() != Role.ADMIN) {
			throw new AccessDeniedException("Access denied");
		}

		if (userRepository.findByEmail(request.getEmail()).isPresent()) {
			throw new ResourceAlreadyExistsException("Email already exists");
		}

		if (request.getRole() == Role.ADMIN && !request.getEmail().endsWith("@vaultspay.com")) {
			throw new IllegalArgumentException("Admin email must end with @vaultspay.com");
		}

		User user = new User();
		user.setFirstName(request.getFirstName());
		user.setLastName(request.getLastName());
		user.setPhoneNumber(request.getPhoneNumber());
		user.setEmail(request.getEmail());
		user.setPassword(passwordEncoder.encode(request.getPassword()));
		user.setRole(request.getRole());

		userRepository.save(user);

		return ResponseEntity.ok(Map.of("message", user.getRole() + " created successfully", "user", new UserResponse(
				user.getFirstName() + " " + user.getLastName(), user.getEmail(), user.getPhoneNumber())));
	}

	public ResponseEntity<?> refreshAccessToken(String refreshToken) {
		if (!jwtService.isRefreshToken(refreshToken)) {
			throw new AccessDeniedException("Invalid refresh token");
		}

		String id = jwtService.extractUserId(refreshToken);
		User user = userRepository.findByEmail(id)
				.orElseThrow(() -> new ResourceNotFoundException("User not found"));

		String newAccessToken = jwtService.generateToken(user.getEmail(), user.getId(), user.getRole());

		return ResponseEntity.ok(Map.of("accessToken", newAccessToken));
	}
}
