package com.example.account.service;
import com.example.account.dto.AccountDto;
import com.example.account.exception.ResourceAlreadyExistsException;
import com.example.account.exception.ResourceNotFoundException;
import com.example.account.mapper.AccountMapper;
import com.example.account.model.AccountModel;
import com.example.account.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountDto createAccount(AccountDto accountDTO) {
        accountRepository.findByUserId(accountDTO.getUserId())
                .ifPresent(a -> {
                    throw new ResourceAlreadyExistsException("Account already exists for this user");
                });

        AccountModel model = AccountMapper.toModel(accountDTO);

        model.setBalance(BigDecimal.ZERO);

        model.setAccountNumber(generateUniqueAccountNumber());

        AccountModel saved = accountRepository.save(model);
        log.info("Account created for userId={}, accountNumber={}", saved.getUserId(), saved.getAccountNumber());
        return AccountMapper.toDTO(saved);
    }

    public AccountDto getAccountByUserId(Long userId) {
        AccountModel model = accountRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));
        return AccountMapper.toDTO(model);
    }

    public List<AccountDto> getAllAccounts() {
        return accountRepository.findAll().stream()
                .map(AccountMapper::toDTO)
                .collect(Collectors.toList());
    }

    public AccountDto updateAccount(Long userId, AccountDto updatedDTO) {
        AccountModel existing = accountRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));

       existing.setStatus(updatedDTO.getStatus());

        AccountModel updated = accountRepository.save(existing);
        log.info("Account updated for userId={}", userId);
        return AccountMapper.toDTO(updated);
    }

    public void deleteAccount(Long userId) {
        AccountModel existing = accountRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));
        accountRepository.delete(existing);
        log.info("Account deleted for userId={}", userId);
    }

    private String generateUniqueAccountNumber() {
        String accountNumber;
        do {
            accountNumber = "ACC" + System.currentTimeMillis() + (int)(Math.random() * 1000);
        } while (accountRepository.findByAccountNumber(accountNumber).isPresent());
        return accountNumber;
    }
}
