
package com.example.demo.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.example.demo.enums.TransactionStatus;
import com.example.demo.enums.TransactionType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionHistoryResponseDTO {
    private Long id;
    private BigDecimal amount;
    private TransactionType type;
    private TransactionStatus status;
    private String narration;
    private String transactionRefId;
    private LocalDateTime createdAt;
  private AccountInfoDTO senderAccount;
  private AccountInfoDTO receiverAccount;
	/*
	 * private Long senderAccountId; private Long receiverAccountId;
	 */

    
}
