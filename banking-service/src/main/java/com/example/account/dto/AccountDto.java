package com.example.account.dto;
import com.example.account.model.enums.AccountStatus;
import com.example.account.model.enums.Currency;
import java.math.BigDecimal;
import java.time.LocalDateTime;
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
