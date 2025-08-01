package com.example.demo.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.example.demo.dto.AccountDto;
import com.example.demo.dto.UserDto;
import com.example.demo.enums.AccountStatus;
import com.example.demo.enums.Currency;
import com.example.demo.exception.ResourceAlreadyExistsException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.mapper.AccountMapper;
import com.example.demo.model.Account;
import com.example.demo.repository.AccountRepository;
import com.example.demo.util.AdminHelper;
import com.example.demo.util.JwtUtil;
import com.example.demo.util.PaginationResponse;
import com.example.demo.util.UserHelper;
import com.example.demo.util.UserInfoHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService {

	private final AccountRepository accountRepository;
	private final JwtUtil jwtUtil;
	private final RestTemplate restTemplate;

	public AccountDto getAccountById(Long accountId) {
		Account account = accountRepository.findById(accountId)
				.orElseThrow(() -> new ResourceNotFoundException("Account not found"));
		return AccountMapper.toDTO(account);
	}

	public Account getAccountEntityByUserId(Long userId) {
		return accountRepository.findByUserId(userId)
				.orElseThrow(() -> new ResourceNotFoundException("Account not found"));
	}

	public List<AccountDto> getAllAccounts() {
		return accountRepository.findAll().stream().map(AccountMapper::toDTO).collect(Collectors.toList());
	}

	/*
	 * public AccountDto updateAccount(Long userId, AccountDto updatedDTO) { Account
	 * existing = accountRepository.findByUserId(userId) .orElseThrow(() -> new
	 * ResourceNotFoundException("Account not found"));
	 * 
	 * if (updatedDTO.getStatus() != null) {
	 * existing.setStatus(updatedDTO.getStatus()); } else { throw new
	 * IllegalArgumentException("Status cannot be null for update"); }
	 * 
	 * Account updated = accountRepository.save(existing);
	 * log.info("Account updated for userId={}", userId); return
	 * AccountMapper.toDTO(updated); }
	 */
	public AccountDto updateAccount(Long targetUserId, AccountDto updatedDTO, boolean isAdmin, Long requesterUserId) {
	    Account existing = accountRepository.findByUserId(targetUserId)
	            .orElseThrow(() -> new ResourceNotFoundException("Account not found"));

	    // If the requester is not admin, deny any update
	    if (!isAdmin) {
	        throw new IllegalArgumentException("Users are not allowed to update account details.");
	    }

	    // Admin is allowed, but only for status update
	    if (updatedDTO.getStatus() == null) {
	        throw new IllegalArgumentException("Only 'status' update is allowed by admin, and it cannot be null.");
	    }

	    // Check if any disallowed fields are being modified
	    if (updatedDTO.getTitle() != null ||
	        updatedDTO.getCurrency() != null ||
	        updatedDTO.getBalance() != null ||
	        updatedDTO.getAccountNumber() != null ||
	        updatedDTO.getCreatedAt() != null ||
	        updatedDTO.getId() != null ||
	        updatedDTO.getUserId() != null) {

	        throw new IllegalArgumentException("Only 'status' update is allowed. Other fields cannot be modified.");
	    }

	    // Apply status update
	    existing.setStatus(updatedDTO.getStatus());
	    Account updated = accountRepository.save(existing);
	    log.info("Account status updated by admin for userId={}", targetUserId);
	    return AccountMapper.toDTO(updated);
	}

	public Account updateAccount(Account account) {
		return accountRepository.save(account);
	}

	public AccountDto createAccountFromToken(String token) {
		UserDto user = jwtUtil.extractUser(token);

		if (accountRepository.existsByUserId(user.getId())) {
			throw new ResourceAlreadyExistsException("Account already exists for this user.");
		}

		UserDto userInfo = UserInfoHelper.getUserInfo(user.getId(), token, restTemplate);

		Account account = new Account();
		account.setUserId(user.getId());
		account.setTitle(UserHelper.getFullName(userInfo));
		account.setStatus(AccountStatus.ACTIVE);
		account.setBalance(BigDecimal.ZERO);
		account.setAccountNumber(generateUniqueAccountNumber());

		return AccountMapper.toDTO(accountRepository.save(account));
	}

	public PaginationResponse<AccountDto> getAccountsBasedOnRole(String jwt, Pageable pageable) {
		UserDto user = jwtUtil.extractUser(jwt);

		Page<Account> pagedAccounts;

		if (AdminHelper.isAdmin(user)) {
			pagedAccounts = accountRepository.findAll(pageable);
		} else {
			pagedAccounts = accountRepository.findAllByUserId(user.getId(), pageable);
		}

		return PaginationResponse.fromPage(pagedAccounts.map(AccountMapper::toDTO));
	}

	public AccountDto getAccountByIdForUser(Long accountId, String jwt) {
		UserDto user = jwtUtil.extractUser(jwt);

		Account account = accountRepository.findById(accountId)
				.orElseThrow(() -> new ResourceNotFoundException("Account not found"));

		boolean isOwner = account.getUserId().equals(user.getId());

		if (!AdminHelper.isAdmin(user) && !isOwner) {
			throw new ResourceAlreadyExistsException("You are not authorized to view this account.");
		}

		return AccountMapper.toDTO(account);
	}

	public AccountDto updateAccountStatus(Long accountId, AccountDto updatedDTO, String jwt) {
		UserDto user = jwtUtil.extractUser(jwt);

		if (!AdminHelper.isAdmin(user)) {
			throw new ResourceAlreadyExistsException("Only ADMIN can update account status.");
		}

		Account existing = accountRepository.findById(accountId)
				.orElseThrow(() -> new ResourceNotFoundException("Account not found"));

		if (updatedDTO.getStatus() == null) {
			throw new IllegalArgumentException("Status cannot be null.");
		}

		existing.setStatus(updatedDTO.getStatus());
		return AccountMapper.toDTO(accountRepository.save(existing));
	}

	public List<AccountDto> getAccountsByUserId(Long userId) {
		return accountRepository.findAllByUserId(userId).stream().map(AccountMapper::toDTO)
				.collect(Collectors.toList());
	}

	public List<AccountDto> getAccountsForUser(Long userId) {
		return accountRepository.findAllByUserId(userId).stream().map(AccountMapper::toDTO)
				.collect(Collectors.toList());
	}

	public AccountDto getAccountByUserIdAndCurrency(Long userId, Currency currency) {
		Account account = accountRepository.findByUserIdAndCurrency(userId, currency)
				.orElseThrow(() -> new ResourceNotFoundException("Account not found for user and currency"));

		return AccountMapper.toDTO(account);
	}

	private String generateUniqueAccountNumber() {
		String accountNumber;
		do {
			accountNumber = "ACC" + System.currentTimeMillis() + (int) (Math.random() * 1000);
		} while (accountRepository.findByAccountNumber(accountNumber).isPresent());
		return accountNumber;
	}

}
