package com.fintech.authservice.controller;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fintech.authservice.dto.UserCreationRequest;
import com.fintech.authservice.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("v1/auth/users")

public class UserController {
	private final UserService userService;

	@GetMapping
	public ResponseEntity<?> users(@RequestParam(defaultValue = "1") int page,
			@RequestParam(defaultValue = "10") int size) {
		Pageable pageable = PageRequest.of(Math.max(0, page - 1), size);
		return userService.viewUsers(pageable);
	}

	@PostMapping
	public ResponseEntity<?> createUsers(@Valid @RequestBody UserCreationRequest request,
			Authentication authentication) {
		return userService.createUsers(authentication, request);

	}

	@GetMapping("/{id}")
	public ResponseEntity<?> getUserById(@PathVariable Long id, Authentication authentication) {
		return userService.getUserById(id, authentication);
	}

}
