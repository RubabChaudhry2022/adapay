package com.example.demo.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransferRequest {
	private Long senderId;
	private Long receiverId;
	private BigDecimal amount;
	private String currencyCode;
	private String transactionReferenceId;
	private String narration;
}
