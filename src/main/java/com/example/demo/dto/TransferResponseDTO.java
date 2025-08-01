package com.example.demo.dto;

import lombok.Data;

@Data
public class TransferResponseDTO {
    private String referenceId;
    private TransactionHistoryDTO senderTransaction;
    private TransactionHistoryDTO receiverTransaction;
}