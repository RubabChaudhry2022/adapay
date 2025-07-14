package com.example.demo.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.example.demo.dto.DepositRequest;
import com.example.demo.dto.TransactionHistoryDTO;
import com.example.demo.dto.TransferRequest;
import com.example.demo.dto.WithdrawRequest;
import com.example.demo.enums.TransactionStatus;
import com.example.demo.enums.TransactionType;
import com.example.demo.model.Account;
import com.example.demo.model.Payment;
import com.example.demo.repository.PaymentRepository;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class PaymentService {

	private final PaymentRepository paymentRepository;
	private final AccountService accountService;
	private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);

	private String getNarrationOrDefault(String provided, String fallback) {
		return (provided != null && !provided.trim().isEmpty()) ? provided : fallback;
	}

	private String generateTransactionReferenceId() {
		return "TXN-" + System.currentTimeMillis();
	}

	public Payment saveTransaction(Account account, BigDecimal amount, TransactionType type, TransactionStatus status,
			String currencyCode, String narration, String transactionReferenceId, Long senderId, Long receiverId) {
		Payment payment = new Payment();
		payment.setAccount(account);
		payment.setAmount(amount);
		payment.setType(type);
		payment.setStatus(status);
		payment.setCurrencyCode(currencyCode);
		payment.setNarration(narration);
		payment.setTransactionReferenceId(transactionReferenceId);
		payment.setSenderId(senderId);
		payment.setReceiverId(receiverId);
		payment.setCreatedAt(LocalDateTime.now());

		return paymentRepository.save(payment);
	}

	public Payment depositMoney(DepositRequest request) {
		if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
			throw new IllegalArgumentException("Amount must be greater than 0");
		}

		Account account = accountService.getAccountByUserId(request.getUserId());
		account.setBalance(account.getBalance().add(request.getAmount()));
		accountService.updateAccount(account);

		String referenceId = generateTransactionReferenceId();

		return saveTransaction(account, request.getAmount(), TransactionType.DEPOSIT, TransactionStatus.SUCCESS,
				request.getCurrencyCode(), getNarrationOrDefault(request.getNarration(), "Deposit to account"),
				referenceId, null, null);
	}

	public Payment withdrawMoney(WithdrawRequest request) {
		if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
			throw new IllegalArgumentException("Amount must be greater than 0");
		}

		Account account = accountService.getAccountByUserId(request.getUserId());
		if (account == null) {
			throw new RuntimeException("Account not found for user ID: " + request.getUserId());
		}

		if (account.getBalance().compareTo(request.getAmount()) < 0) {
			throw new RuntimeException("Insufficient balance");
		}

		account.setBalance(account.getBalance().subtract(request.getAmount()));

		accountService.updateAccount(account);

		String referenceId = generateTransactionReferenceId();
		return saveTransaction(account, request.getAmount(), TransactionType.WITHDRAW, TransactionStatus.SUCCESS,
				request.getCurrencyCode(), getNarrationOrDefault(request.getNarration(), "Withdrawal from account"),
				referenceId, null, null);
	}

	public List<TransactionHistoryDTO> getTransactionHistory(Long userId) {
		List<Payment> payments = paymentRepository.findByAccountUserId(userId);

		return payments.stream().map(payment -> {
			TransactionHistoryDTO dto = new TransactionHistoryDTO();
			dto.setAmount(payment.getAmount());
			dto.setType(payment.getType());
			dto.setStatus(payment.getStatus());
			dto.setCurrencyCode(payment.getCurrencyCode());
			dto.setNarration(payment.getNarration());
			dto.setReferenceId(payment.getTransactionReferenceId());
			dto.setTimestamp(payment.getCreatedAt());

			dto.setSenderId(payment.getSenderId());
			dto.setReceiverId(payment.getReceiverId());

			return dto;
		}).collect(Collectors.toList());
	}

	public String transferMoney(TransferRequest request) {
		try {
			if (request.getSenderId().equals(request.getReceiverId())) {
				throw new IllegalArgumentException("Sender and receiver cannot be the same");
			}

			Account sender = accountService.getAccountByUserId(request.getSenderId());
			Account receiver = accountService.getAccountByUserId(request.getReceiverId());

			if (sender == null) {
				throw new IllegalArgumentException("Sender account not found");
			}
			if (receiver == null) {
				throw new IllegalArgumentException("Receiver account not found");
			}

			if (sender.getBalance().compareTo(request.getAmount()) < 0) {
				throw new IllegalArgumentException("Insufficient balance");
			}

			sender.setBalance(sender.getBalance().subtract(request.getAmount()));
			receiver.setBalance(receiver.getBalance().add(request.getAmount()));

			accountService.updateAccount(sender);
			accountService.updateAccount(receiver);

			String referenceId = generateTransactionReferenceId();

			saveTransaction(sender, request.getAmount(), TransactionType.TRANSFER, TransactionStatus.SUCCESS,
					request.getCurrencyCode(),
					getNarrationOrDefault(request.getNarration(), "Transfer to user ID: " + request.getReceiverId()),
					referenceId, request.getSenderId(), request.getReceiverId());

			saveTransaction(receiver, request.getAmount(), TransactionType.RECEIVE, TransactionStatus.SUCCESS,
					request.getCurrencyCode(),
					getNarrationOrDefault(request.getNarration(), "Received from user ID: " + request.getSenderId()),
					referenceId, request.getSenderId(), request.getReceiverId());

			return "Transfer successful with reference ID: " + referenceId;

		} catch (IllegalArgumentException e) {
			logger.error("Transfer failed: {}", e.getMessage());
			throw e;
		}
	}
}
