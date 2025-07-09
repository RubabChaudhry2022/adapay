package com.example.Project.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CardDto {
    private Long id;
    private String cardNumber;
    private String cardholderName;
    private String expiryDate;
    private Long userId;
}
