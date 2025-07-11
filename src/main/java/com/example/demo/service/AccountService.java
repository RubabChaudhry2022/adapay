package com.example.demo.service;

import com.example.demo.model.Account;
import com.example.demo.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.Optional;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

  

	
	public Account createAccount(Account account) {
		Optional<Account> existing = accountRepository.findByUserId(account.getUserId());
		if (existing.isPresent()) {
			throw new RuntimeException("Account already exists for this user.");
		}
		return accountRepository.save(account);
	}
	public Account getAccountByUserId(Long userId) {
	    return accountRepository.findByUserId(userId)
	        .orElseThrow(() -> new RuntimeException("Account not found"));
	}

	public Account updateAccount(Account account) {
	    return accountRepository.save(account);
	}


}
