package com.fintech.authservice.service;

import java.util.List;
import java.util.Map;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.fintech.authservice.dto.UserCreationRequest;
import com.fintech.authservice.dto.UserResponse;
import com.fintech.authservice.exception.ResourceAlreadyExistsException;
import com.fintech.authservice.exception.ResourceNotFoundException;
import com.fintech.authservice.model.Role;
import com.fintech.authservice.model.User;
import com.fintech.authservice.repository.UserRepository;
import com.fintech.authservice.util.SecurityHelper;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserService {
	public final UserRepository userRepository;
	private final ModelMapper modelMapper;
	private final PasswordEncoder passwordEncoder;

	public void validateEmailAndPhone(String email, String phone) {
		if (userRepository.findByEmail(email).isPresent()) {
			throw new ResourceAlreadyExistsException("Email already exists");
		}
		if (userRepository.findByPhoneNumber(phone).isPresent()) {
			throw new ResourceAlreadyExistsException("Phone number already exists");
		}
	}

	public User buildAndSaveUser(Object request, String rawPassword, Role role) {
		User user = modelMapper.map(request, User.class);
		user.setPassword(passwordEncoder.encode(rawPassword));
		user.setRole(role);
		return userRepository.save(user);
	}

	private User getAuthenticatedUser() {
		Long userId = SecurityHelper.getCurrentUserId();
		return userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found"));
	}

	private void checkAdmin() {
		if (!SecurityHelper.isAdmin()) {
			throw new AccessDeniedException("Access denied");
		}
	}

	public ResponseEntity<?> getUserById(Long id, Authentication authentication) {
		User targetUser = userRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("User not found"));

		Long currentUserId = SecurityHelper.getCurrentUserId();
		if (currentUserId.equals(id) || SecurityHelper.isAdmin()) {
			return ResponseEntity.ok(targetUser);
		}
		throw new AccessDeniedException("Access denied");
	}

	public ResponseEntity<?> viewUsers(Pageable pageable) {
		User currentUser = getAuthenticatedUser();

		if (SecurityHelper.isAdmin()) {
			// Admin can view all users
			Page<User> userPage = userRepository.findAll(pageable);
			List<UserResponse> users = userPage.getContent().stream().map(UserResponse::from).toList();
			return ResponseEntity.ok(Map.of("data", users, "pageInfo",
					Map.of("currentPage", userPage.getNumber() + 1, "totalPages", userPage.getTotalPages())));
		} else {
			// User can only view their own info
			UserResponse userResponse = UserResponse.from(currentUser);
			return ResponseEntity.ok(Map.of("data", List.of(userResponse)));
		}
	}

	public ResponseEntity<?> createUsers(Authentication authentication, UserCreationRequest request) {
		checkAdmin();

		validateEmailAndPhone(request.getEmail(), request.getPhoneNumber());

		Role role = request.getRole() != null ? request.getRole() : Role.USER;
		User user = buildAndSaveUser(request, request.getPassword(), role);
		UserResponse response = UserResponse.from(user);

		return ResponseEntity.ok(Map.of("message", role + " created successfully", "user", response));
	}
}
