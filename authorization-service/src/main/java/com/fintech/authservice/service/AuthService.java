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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Service

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

		// Assign role based on email domain
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
	/*
	 * public ResponseEntity<?> login(LoginRequest request) { try { User user =
	 * userRepository.findByEmail(request.getEmail()) .orElseThrow(() -> new
	 * ResourceNotFoundException("User not found"));
	 * 
	 * if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
	 * throw new AccessDeniedException("Invalid credentials"); }
	 * 
	 * String accessToken = jwtService.generateToken(user.getEmail(), user.getId(),
	 * user.getRole()); String refreshToken =
	 * jwtService.generateRefreshToken(user.getEmail(), user.getId(),
	 * user.getRole()); Date expiration = jwtService.extractExpiration(accessToken);
	 * 
	 * SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	 * 
	 * return ResponseEntity.ok(Map.of( "message", "Login successful",
	 * "accessToken", accessToken, "refreshToken", refreshToken, "expiresAt",
	 * sdf.format(expiration) ));
	 * 
	 * } catch (ResourceNotFoundException | AccessDeniedException e) { // known
	 * exceptions return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
	 * "error", e.getMessage() )); } catch (Exception e) { // unexpected exception
	 * e.printStackTrace(); // to log in console return
	 * ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of( "error",
	 * "An unexpected error occurred" )); } }
	 */

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

		return ResponseEntity.ok(userRepository.findAll());
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

		String email = jwtService.extractEmail(refreshToken);
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException("User not found"));
		String newAccessToken = jwtService.generateToken(user.getEmail(), user.getId(), user.getRole());

		return ResponseEntity.ok(Map.of("accessToken", newAccessToken));
	}

}
