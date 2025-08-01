package com.example.demo.dto;

import com.example.demo.enums.AccountStatus;
import com.example.demo.enums.Currency;
import com.example.demo.model.Account;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountInfoDTO {
    private Long id;
    private String title;
    private String accountNumber;
    private Currency currency;
    private AccountStatus status;

    public AccountInfoDTO(Account account) {
        this.id = account.getId();
        this.title = account.getTitle();
        this.accountNumber = account.getAccountNumber();
        this.currency = account.getCurrency();
        this.status = account.getStatus();
    }
	
}
