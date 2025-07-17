package com.example.demo.dto;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class WithdrawRequest {
	private Long userId;
	private BigDecimal amount;
	private String currencyCode;
	private String transactionReferenceId;
	private String narration;
	private String cardNumber;
	private String cardPin;
}
