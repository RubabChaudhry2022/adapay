package com.example.demo.dto;

import java.math.BigDecimal;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PurchaseRequest {
    @NotNull
    private Long userId;

    @NotNull
    private BigDecimal amount;

    private String currencyCode;
    private String narration;
    private String cardNumber;
    private String cardPin;
}
