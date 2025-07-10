package com.example.Account;

import com.example.Account.AccountModel.AccountModel;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
public class AccountService {

    public List<AccountModel> getAllAccounts() {
        return Arrays.asList(
            new AccountModel(1L, 101L, new BigDecimal("5000.00"), "Uswa", "PKR", "ACTIVE", LocalDateTime.now()),
            new AccountModel(2L, 102L, new BigDecimal("8000.00"), "Samiya", "PKR", "INACTIVE", LocalDateTime.now().minusDays(1))
        );
    }

    public AccountModel getAccountByUserId(Long userId) {
        return new AccountModel(3L, userId, new BigDecimal("12000.00"), "Mock User", "PKR", "ACTIVE", LocalDateTime.now().minusDays(2));
    }
}
