package com.example.Card_Service_V2.services.dtos;

public class InternalCardVerificationRequestDTO {
    private String cardNumber;
    private String cardPin;
    private Integer userId; 
    public InternalCardVerificationRequestDTO() {}

    public InternalCardVerificationRequestDTO(String cardNumber, String cardPin) {
        this.cardNumber = cardNumber;
        this.cardPin = cardPin;
    }

    public InternalCardVerificationRequestDTO(String cardNumber, String cardPin, Integer userId) {
        this.cardNumber = cardNumber;
        this.cardPin = cardPin;
        this.userId = userId;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCardPin() {
        return cardPin;
    }

    public void setCardPin(String cardPin) {
        this.cardPin = cardPin;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
}
