package com.example.demo.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.example.demo.enums.TransactionType;
import com.example.demo.enums.TransactionStatus;
import lombok.Data;

@Data
public class TransactionHistoryDTO {
	private BigDecimal amount;
	private TransactionType type;
	private TransactionStatus status;
	private String currencyCode;
	private String narration;
	private String referenceId;
	private LocalDateTime timestamp;
	private Long senderId;
	private Long receiverId;
}
