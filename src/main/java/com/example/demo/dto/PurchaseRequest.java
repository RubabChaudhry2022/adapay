package com.example.demo.dto;
import java.math.BigDecimal;

import com.example.demo.enums.Currency;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PurchaseRequest {
    @NotNull(message = " Account ID is required.")
    private Long accountId;

    @NotNull(message = " Amount is required.")
    private BigDecimal amount;

    @NotNull(message = " Currency Code is required.")
    private Currency currencyCode; 
    
    private String narration;

    @NotNull(message = " Card Number is required.")
    private String cardNumber;


    @NotNull(message = "Cvv is required.")
    private String cvv;
    
    private String transactionRefId;
    
    
}
