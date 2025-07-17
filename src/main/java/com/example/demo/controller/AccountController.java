package com.example.demo.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.demo.dto.AccountDto;
import com.example.demo.dto.UserDto;
import com.example.demo.service.AccountService;
import com.example.demo.util.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
public class AccountController {

	private final AccountService accountService;
	private final JwtUtil jwtUtil;

	@PostMapping
	public ResponseEntity<AccountDto> createAccount(@RequestHeader("Authorization") String token) {
		if (!isValidToken(token))
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		String jwt = token.substring(7);
		AccountDto created = accountService.createAccountFromToken(jwt);
		return new ResponseEntity<>(created, HttpStatus.CREATED);
	}

	@GetMapping("/me")
	public ResponseEntity<AccountDto> getMyAccount(@RequestHeader("Authorization") String token) {
		if (token == null || !token.startsWith("Bearer ")) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		String jwt = token.substring(7);
		UserDto user = jwtUtil.extractUser(jwt); // Only parses user, doesn't check role
		return ResponseEntity.ok(accountService.getAccountByUserId(user.getId()));
	}

	@GetMapping
	public ResponseEntity<List<AccountDto>> getAllAccounts(@RequestHeader("Authorization") String token) {
		if (!isValidToken(token))
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		String jwt = token.substring(7);
		UserDto user = jwtUtil.extractUser(jwt);
		if (!"ADMIN".equals(user.getRole()))
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		return ResponseEntity.ok(accountService.getAllAccounts());
	}

	@GetMapping("/{userId}")
	public ResponseEntity<AccountDto> getAccountByUserId(@PathVariable Long userId,
			@RequestHeader("Authorization") String token) {
		if (!isValidToken(token))
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		String jwt = token.substring(7);
		UserDto user = jwtUtil.extractUser(jwt);
		if (!"ADMIN".equals(user.getRole()))
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		return ResponseEntity.ok(accountService.getAccountByUserId(userId));
	}

	@PutMapping("/{userId}")
	public ResponseEntity<AccountDto> updateAccountStatus(@PathVariable Long userId,
			@Valid @RequestBody AccountDto accountDTO, @RequestHeader("Authorization") String token) {
		if (!isValidToken(token))
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		String jwt = token.substring(7);
		UserDto user = jwtUtil.extractUser(jwt);
		if (!"ADMIN".equals(user.getRole()))
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		return ResponseEntity.ok(accountService.updateAccount(userId, accountDTO));
	}

	private boolean isValidToken(String token) {
		return token != null && token.startsWith("Bearer ");
	}
}
