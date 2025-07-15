package com.example.account.mapper;
import com.example.account.dto.AccountDto;
import com.example.account.model.AccountModel;

public class AccountMapper {

    public static AccountDto toDTO(AccountModel model) {
        AccountDto dto = new AccountDto();
        dto.setId(model.getId());
        dto.setUserId(model.getUserId());
        dto.setTitle(model.getTitle());
        dto.setAccountNumber(model.getAccountNumber());
        dto.setBalance(model.getBalance());
        dto.setCurrency(model.getCurrency());
        dto.setStatus(model.getStatus());
        dto.setCreatedAt(model.getCreatedAt());
        return dto;
    }

    public static AccountModel toModel(AccountDto dto) {
        AccountModel model = new AccountModel();
        model.setUserId(dto.getUserId());
        model.setTitle(dto.getTitle());
        model.setAccountNumber(dto.getAccountNumber());
        model.setBalance(dto.getBalance());
        model.setCurrency(dto.getCurrency());
        model.setStatus(dto.getStatus());
        model.setCreatedAt(dto.getCreatedAt());
        return model;
    }
}
