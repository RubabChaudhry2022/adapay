package com.example.Card_Service_V2.services.dtos;

/**
 * DTO for card creation requests.
 * NOTE: userId is extracted from the JWT token by the controller and should not be included in the request body.
 * Any userId value in the request body will be ignored for security reasons.
 */
public class CreateCardRequestDTO {
    
    private Integer userId; // Set server-side from JWT token, not from request body
    private Integer accountId;
    private String type;
    private String network;
    private String cardpin;

    public CreateCardRequestDTO() {
    }

    public CreateCardRequestDTO(Integer userId, Integer accountId, String type, String network, String cardpin) {
        this.userId = userId;
        this.accountId = accountId;
        this.type = type;
        this.network = network;
        this.cardpin = cardpin;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getAccountId() {
        return accountId;
    }

    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getNetwork() {
        return network;
    }

    public void setNetwork(String network) {
        this.network = network;
    }

    public String getCardpin() {
        return cardpin;
    }

    public void setCardpin(String cardpin) {
        this.cardpin = cardpin;
    }

    @Override
    public String toString() {
        return "CreateCardRequestDTO{" +
                "userId=" + userId + " (set from JWT)" +
                ", accountId=" + accountId +
                ", type='" + type + '\'' +
                ", network='" + network + '\'' +
                ", cardpin='***'" + 
                '}';
    }
}
