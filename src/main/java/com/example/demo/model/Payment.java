package com.example.demo.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.example.demo.enums.TransactionStatus;
import com.example.demo.enums.TransactionType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "payments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "sender_account_id")
	private Long senderAccountId;

	@Column(name = "receiver_account_id")
	private Long receiverAccountId;

	@NotNull
	private BigDecimal amount;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private TransactionType type;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private TransactionStatus status = TransactionStatus.SUCCESS;

	private String narration;

	@Column(name = "transaction_ref_id", unique = true)
	private String transactionRefId;

	@Column(name = "created_at")
	private LocalDateTime createdAt = LocalDateTime.now();
}