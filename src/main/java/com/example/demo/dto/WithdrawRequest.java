package com.example.demo.dto;

import java.math.BigDecimal;
import com.example.demo.enums.Currency;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class WithdrawRequest {
	@NotNull(message = "Account ID is required.")
	private Long accountId;

	
	//@DecimalMin(value = "0.01", message = "Amount must be greater than zero")
    @NotNull(message = " Amount is required.")
	private BigDecimal amount;

	@NotNull(message = "Currency Code is required.")
	private Currency currencyCode;

	private String narration;
	@NotNull(message = "Card Number is required.")
	private String cardNumber;

	@NotNull(message = "Card Pin is required.")
	private String cardPin;

	private String transactionRefId;
}