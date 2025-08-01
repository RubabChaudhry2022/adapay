package com.example.demo.dto;

import java.math.BigDecimal;

import com.example.demo.enums.Currency;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TransferRequest {
	
	@NotNull(message = " Sender Account ID is required.")
	private Long senderAccountId;
	
	@NotNull(message = " Receiver Account ID is required.")
	private Long receiverAccountId;
	
	@NotNull(message = " Amount is required.")
	private BigDecimal amount;
	
	@NotNull(message = " Currency Code is required.")
	private Currency currencyCode;
	
	private String transactionReferenceId;
	private String narration;
}
