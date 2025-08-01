package com.example.demo.dto;

import java.math.BigDecimal;
import com.example.demo.enums.TransactionStatus;
import com.example.demo.enums.TransactionType;
import lombok.Data;

@Data
public class TransactionSaveRequest {
	private Long senderAccountId;
	private Long receiverAccountId;
	private BigDecimal amount;
	private TransactionType type;
	private TransactionStatus status;
	private String narration;
	private String transactionRefId;
}