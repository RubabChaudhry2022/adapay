package com.example.demo.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.example.demo.enums.TransactionStatus;
import com.example.demo.enums.TransactionType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
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

	private Long senderId;
	private Long receiverId;

	@NotNull
	@Enumerated(EnumType.STRING)
	private TransactionStatus status;

	@NotNull
	@Enumerated(EnumType.STRING)
	private TransactionType type;

	private String currencyCode;

	@Column(nullable = false, unique = true)
	private String transactionReferenceId;

	private String narration;

	@NotNull
	@DecimalMin(value = "0.01", inclusive = true, message = "Amount must be greater than 0")
	@Column(nullable = false)
	private BigDecimal amount;

	private LocalDateTime createdAt = LocalDateTime.now();


	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sender_account_id")
	private Account senderAccount;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "receiver_account_id")
	private Account receiverAccount;
}

