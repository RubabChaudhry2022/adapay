package com.example.demo.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import com.example.demo.dto.AccountDto;
import com.example.demo.dto.UserDto;
import com.example.demo.enums.AccountStatus;
import com.example.demo.enums.Currency;
import com.example.demo.exception.ResourceAlreadyExistsException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.mapper.AccountMapper;
import com.example.demo.model.Account;
import com.example.demo.repository.AccountRepository;
import com.example.demo.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService {

	private final AccountRepository accountRepository;
	private final JwtUtil jwtUtil;

	public AccountDto createAccountFromToken(String token) {
		UserDto user = jwtUtil.extractUser(token);
		log.info("Creating account for user: {}", user);

		accountRepository.findByUserId(user.getId()).ifPresent(existing -> {
			throw new ResourceAlreadyExistsException("Account already exists for this user");
		});

		String title = (user.getEmail() == null || user.getEmail().isBlank()) ? "User-" + user.getId()
				: user.getEmail();

		Account account = new Account();
		account.setUserId(user.getId());
		account.setTitle(title);
		account.setAccountNumber(generateUniqueAccountNumber());
		account.setBalance(BigDecimal.ZERO);
		account.setCurrency(Currency.PKR);
		account.setStatus(AccountStatus.ACTIVE);

		Account saved = accountRepository.save(account);
		log.info("Account created: {}", saved);
		return AccountMapper.toDTO(saved);
	}

	public AccountDto getAccountByUserId(Long userId) {
		Account account = accountRepository.findByUserId(userId)
				.orElseThrow(() -> new ResourceNotFoundException("Account not found"));
		return AccountMapper.toDTO(account);
	}

	// ************
	public Account getAccountEntityByUserId(Long userId) {
		return accountRepository.findByUserId(userId)
				.orElseThrow(() -> new ResourceNotFoundException("Account not found"));
	}

	public List<AccountDto> getAllAccounts() {
		return accountRepository.findAll().stream().map(AccountMapper::toDTO).collect(Collectors.toList());
	}

	public AccountDto updateAccount(Long userId, AccountDto updatedDTO) {
		Account existing = accountRepository.findByUserId(userId)
				.orElseThrow(() -> new ResourceNotFoundException("Account not found"));

		if (updatedDTO.getStatus() != null) {
			existing.setStatus(updatedDTO.getStatus());
		} else {
			throw new IllegalArgumentException("Status cannot be null for update");
		}

		Account updated = accountRepository.save(existing);
		log.info("Account updated for userId={}", userId);
		return AccountMapper.toDTO(updated);
	}

	public Account updateAccount(Account account) {
		return accountRepository.save(account);
	}

	private String generateUniqueAccountNumber() {
		String accountNumber;
		do {
			accountNumber = "ACC" + System.currentTimeMillis() + (int) (Math.random() * 1000);
		} while (accountRepository.findByAccountNumber(accountNumber).isPresent());
		return accountNumber;
	}
}
