package com.example.demo.mapper;

import com.example.demo.dto.AccountDto;
import com.example.demo.dto.AccountSummaryDto;
import com.example.demo.model.Account;

public class AccountMapper {

	public static AccountDto toDTO(Account accountDto) {
		AccountDto dto = new AccountDto();
		dto.setId(accountDto.getId());
		dto.setUserId(accountDto.getUserId());
		dto.setTitle(accountDto.getTitle());
		dto.setAccountNumber(accountDto.getAccountNumber());
		dto.setBalance(accountDto.getBalance());
		dto.setCurrency(accountDto.getCurrency());
		dto.setStatus(accountDto.getStatus());
		dto.setCreatedAt(accountDto.getCreatedAt());
		return dto;
	}

	public static Account toModel(AccountDto dto) {
		Account model = new Account();
		model.setUserId(dto.getUserId());
		model.setTitle(dto.getTitle());
		model.setAccountNumber(dto.getAccountNumber());
		model.setBalance(dto.getBalance());
		model.setCurrency(dto.getCurrency());
		model.setStatus(dto.getStatus());
		model.setCreatedAt(dto.getCreatedAt());
		return model;
	}

	public static AccountSummaryDto toSummary(AccountDto dto) {
		return new AccountSummaryDto(dto.getId(), dto.getUserId(), dto.getCurrency(), dto.getAccountNumber(),
				dto.getStatus());
	}
}