package com.example.demo.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.example.demo.enums.TransactionStatus;
import com.example.demo.enums.TransactionType;
import com.example.demo.model.Account;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransactionHistoryDTO {
	private Long id;
	private BigDecimal amount;
	private TransactionType type;
	private TransactionStatus status;
	private String narration;
	private String transactionRefId;
	private LocalDateTime createdAt;
	private Account senderAccount;
	private Account receiverAccount;
}
