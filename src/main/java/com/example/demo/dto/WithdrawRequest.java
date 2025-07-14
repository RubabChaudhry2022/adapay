package com.example.demo.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class WithdrawRequest {
	private Long userId;
	private BigDecimal amount;
	private String currencyCode;
	private String transactionReferenceId;
	private String narration;
}
