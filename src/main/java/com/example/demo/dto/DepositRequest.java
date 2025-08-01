package com.example.demo.dto;

import java.math.BigDecimal;
import com.example.demo.enums.Currency;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DepositRequest {

	@NotNull(message = "Account ID is required.")
	private Long accountId;

	@NotNull(message = "Amount is required.")
	private BigDecimal amount;

	private String narration;

	@NotNull(message = "Currency Code is required.")
	private Currency currencyCode;
	
	private String transactionRefId;

}
