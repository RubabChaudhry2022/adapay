package com.example.demo.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.example.demo.enums.AccountStatus;
import com.example.demo.enums.Currency;

import lombok.Data;
@Data
public class AccountDto {
    private Long id;
    private Long userId;
    private String title;
    private String accountNumber;
    private BigDecimal balance;
    private Currency currency;
    private AccountStatus status;
    private LocalDateTime createdAt;
}
