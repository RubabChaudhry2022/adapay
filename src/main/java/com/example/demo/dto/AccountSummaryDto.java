package com.example.demo.dto;

import com.example.demo.enums.AccountStatus;
import com.example.demo.enums.Currency;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountSummaryDto {

	private Long accountId;
	private Long userId;
	private Currency currency;
	private String accountNumber;
	private AccountStatus status;
}
