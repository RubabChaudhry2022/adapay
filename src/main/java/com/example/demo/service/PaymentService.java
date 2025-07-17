package com.example.demo.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.example.demo.dto.CardVerificationRequest;
import com.example.demo.dto.DepositRequest;
import com.example.demo.dto.PurchaseRequest;
import com.example.demo.dto.TransactionHistoryDTO;
import com.example.demo.dto.TransferRequest;
import com.example.demo.dto.WithdrawRequest;
import com.example.demo.enums.TransactionStatus;
import com.example.demo.enums.TransactionType;
import com.example.demo.model.Account;
import com.example.demo.model.GlobalLedgerEntry;
import com.example.demo.model.Payment;
import com.example.demo.repository.GlobalLedgerRepository;
import com.example.demo.repository.PaymentRepository;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class PaymentService {

	private final PaymentRepository paymentRepository;
	private final AccountService accountService;
	private final GlobalLedgerRepository globalLedgerRepository;
	private RestTemplate restTemplate;
	private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);

	
	private void logToGlobalLedger(Payment payment, BigDecimal signedAmount) {
		GlobalLedgerEntry entry = new GlobalLedgerEntry();
		entry.setTransactionReferenceId(payment.getTransactionReferenceId());
		entry.setType(payment.getType());
		entry.setStatus(payment.getStatus());
		entry.setAmount(signedAmount);
		entry.setSenderId(payment.getSenderId());
		entry.setReceiverId(payment.getReceiverId());
		entry.setNarration(payment.getNarration());
		entry.setCreatedAt(payment.getCreatedAt());

		globalLedgerRepository.save(entry);
	}

	private String getNarrationOrDefault(String provided, String fallback) {
		return (provided != null && !provided.trim().isEmpty()) ? provided : fallback;
	}

	private String generateTransactionReferenceId() {
		return "TXN-" + System.currentTimeMillis();
	}

	public Payment saveTransaction(BigDecimal amount, TransactionType type, TransactionStatus status,
			String currencyCode, String narration, String transactionReferenceId, Long senderId, Long receiverId,
			Account senderAccount, Account receiverAccount) {
		Payment payment = new Payment();
		payment.setAmount(amount);
		payment.setType(type);
		payment.setStatus(status);
		payment.setCurrencyCode(currencyCode);
		payment.setNarration(narration);
		payment.setTransactionReferenceId(transactionReferenceId);
		payment.setSenderId(senderId);
		payment.setReceiverId(receiverId);
		payment.setSenderAccount(senderAccount);
		payment.setReceiverAccount(receiverAccount);
		payment.setCreatedAt(LocalDateTime.now());

		return paymentRepository.save(payment);
	}

	public Payment depositMoney(DepositRequest request) {
		if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
			throw new IllegalArgumentException("Amount must be greater than 0");
		}

		Account account = accountService.getAccountEntityByUserId(request.getUserId());
		if (account == null) {
			throw new IllegalArgumentException("Account not found for user ID: " + request.getUserId());
		}
		account.setBalance(account.getBalance().add(request.getAmount()));
		accountService.updateAccount(account);

		String referenceId = generateTransactionReferenceId();

		Payment saved = saveTransaction(request.getAmount(), TransactionType.DEPOSIT, TransactionStatus.SUCCESS,
				request.getCurrencyCode(), getNarrationOrDefault(request.getNarration(), "Deposit to account"),
				referenceId, account.getUserId(), null, account, null);
		logToGlobalLedger(saved, request.getAmount());
		return saved;
	}

	public Payment withdrawMoney(WithdrawRequest request) {

		if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
			throw new IllegalArgumentException("Amount must be greater than 0");
		}

		Account account = accountService.getAccountEntityByUserId(request.getUserId());
		if (account == null) {
			throw new IllegalArgumentException("Account not found for user ID: " + request.getUserId());
		}

		if (request.getCardNumber() != null && !request.getCardNumber().isEmpty()) {
			String cardServiceUrl = "http://192.168.100.146:8080/api/v1/cards/internal/verify";
			CardVerificationRequest cardRequest = new CardVerificationRequest(request.getCardNumber(),
					request.getCardPin(), request.getUserId());

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<CardVerificationRequest> entity = new HttpEntity<>(cardRequest, headers);

			Boolean isValid = restTemplate.postForObject(cardServiceUrl, entity, Boolean.class);
			if (isValid == null || !isValid) {
				throw new IllegalArgumentException("Card verification failed");
			}
		}
		if (account.getBalance().compareTo(request.getAmount()) < 0) {
			throw new IllegalArgumentException("Insufficient balance");
		}

		account.setBalance(account.getBalance().subtract(request.getAmount()));

		accountService.updateAccount(account);

		String referenceId = generateTransactionReferenceId();
		Payment saved = saveTransaction(request.getAmount(), TransactionType.WITHDRAW, TransactionStatus.SUCCESS,
				request.getCurrencyCode(), getNarrationOrDefault(request.getNarration(), "Withdrawal from account"),
				referenceId, request.getUserId(), null, account, null);
		logToGlobalLedger(saved, request.getAmount().negate());
		return saved;
	}

	public List<TransactionHistoryDTO> getTransactionHistory(Long userId) {
		List<Payment> payments = paymentRepository.findByUserIdInSenderOrReceiver(userId);
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

			Account sender = accountService.getAccountEntityByUserId(request.getSenderId());
			if (sender == null) {
				throw new IllegalArgumentException("Sender account not found");
			}

			Account receiver = accountService.getAccountEntityByUserId(request.getReceiverId());
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

			Payment senderTx = saveTransaction(request.getAmount(), TransactionType.TRANSFER, TransactionStatus.SUCCESS,
					request.getCurrencyCode(),
					getNarrationOrDefault(request.getNarration(), "Transfer to user ID: " + request.getReceiverId()),
					referenceId, request.getSenderId(), request.getReceiverId(), sender, receiver);
			logToGlobalLedger(senderTx, request.getAmount().negate());

			Payment receiverTx = saveTransaction(request.getAmount(), TransactionType.RECEIVE,
					TransactionStatus.SUCCESS, request.getCurrencyCode(),
					getNarrationOrDefault(request.getNarration(), "Received from user ID: " + request.getSenderId()),
					referenceId, request.getSenderId(), request.getReceiverId(), sender, receiver);
			logToGlobalLedger(receiverTx, request.getAmount());

			return "Transfer successful with reference ID: " + referenceId;

		} catch (IllegalArgumentException e) {
			logger.error("Transfer failed: {}", e.getMessage());
			throw e;
		}
	}

	public Payment purchase(PurchaseRequest request) {
		if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
			throw new IllegalArgumentException("Amount must be greater than 0");
		}

		Account account = accountService.getAccountEntityByUserId(request.getUserId());
		if (account == null) {
			throw new IllegalArgumentException("Account not found for user ID: " + request.getUserId());
		}
		if (request.getCardNumber() != null && !request.getCardNumber().isEmpty()) {
			String cardServiceUrl = "http://192.168.100.146:8080/api/v1/cards/internal/verify";
			CardVerificationRequest cardRequest = new CardVerificationRequest(request.getCardNumber(),
					request.getCardPin(), request.getUserId());

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<CardVerificationRequest> entity = new HttpEntity<>(cardRequest, headers);

			Boolean isValid = restTemplate.postForObject(cardServiceUrl, entity, Boolean.class);
			if (isValid == null || !isValid) {
				throw new IllegalArgumentException("Card verification failed");
			}
		}
		if (account.getBalance().compareTo(request.getAmount()) < 0) {
			throw new IllegalArgumentException("Insufficient balance");
		}

		account.setBalance(account.getBalance().subtract(request.getAmount()));
		accountService.updateAccount(account);

		String referenceId = generateTransactionReferenceId();

		Payment saved = saveTransaction(request.getAmount(), TransactionType.PURCHASE, TransactionStatus.SUCCESS,
				request.getCurrencyCode(), getNarrationOrDefault(request.getNarration(), "Purchase made"), referenceId,
				request.getUserId(), null, account, null);
		logToGlobalLedger(saved, request.getAmount().negate());
		return saved;
	}

}
