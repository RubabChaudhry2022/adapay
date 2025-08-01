package com.example.demo.service.helper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import com.example.demo.dto.AccountInfoDTO;
import com.example.demo.dto.CardVerificationRequest;
import com.example.demo.dto.TransactionHistoryDTO;
import com.example.demo.dto.TransactionHistoryResponseDTO;
import com.example.demo.enums.AccountStatus;
import com.example.demo.enums.TransactionStatus;
import com.example.demo.enums.TransactionType;
import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.ForbiddenException;
import com.example.demo.exception.NotFoundException;
import com.example.demo.model.Account;
import com.example.demo.model.Payment;
import com.example.demo.repository.AccountRepository;
import com.example.demo.repository.PaymentRepository;
import com.example.demo.service.DeclinedTransactionLogger;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class TransactionUtils {

	private final AccountRepository accountRepository;
	private final PaymentRepository paymentRepository;
	private final DeclinedTransactionLogger declinedTransactionLogger;
	private final RestTemplate restTemplate;
	public static final BigDecimal MAX_TRANSACTION_AMOUNT = new BigDecimal("100000");
	String cardServiceUrl = "http://192.168.100.146:8081/api/v1/cards/internal/verify";

	public boolean isGlobalAccountSafe(Long accountId) {
		if (accountId == null)
			return false;
		return accountRepository.findById(accountId).map(Account::isGlobalAccount).orElse(false);
	}

	public Account safeFindAccount(Long id) {
		if (id == null)
			return null;
		return accountRepository.findById(id).orElse(null);
	}

	public void validateMaxAmount(BigDecimal amount, TransactionType type, Account sender, Account receiver,
			String transactionRefId) {

		if (amount.compareTo(MAX_TRANSACTION_AMOUNT) > 0) {
			declinedTransactionLogger.saveDeclinedTransaction(sender != null ? sender.getId() : null,
					receiver != null ? receiver.getId() : null, amount, type,
					"Transaction amount exceeds the allowed limit.", transactionRefId);
			throw new BadRequestException("Transaction amount exceeds the allowed limit.");
		}
	}

	public Account getAccountOrNull(Long accountId) {
		if (accountId == null)
			return null;
		return accountRepository.findById(accountId).orElse(null);
	}

	public Payment saveTransaction(BigDecimal amount, TransactionType type, TransactionStatus status, String narration,
			String refId, Long senderId, Long receiverId, Account sender, Account receiver) {
		Payment payment = new Payment();
		payment.setAmount(amount);
		payment.setType(type);
		payment.setStatus(status);
		payment.setNarration(narration);
		payment.setTransactionRefId(refId);
		payment.setSenderAccountId(senderId);
		payment.setReceiverAccountId(receiverId);
		payment.setCreatedAt(LocalDateTime.now());

		try {
			Payment saved = paymentRepository.save(payment);
			paymentRepository.flush();
			return saved;
		} catch (Exception e) {
			log.error(" Failed to save transaction: {}", payment, e);
			throw e;
		}
	}

	public void validateAmount(BigDecimal amount, TransactionType type, Long senderId, Long receiverId,
			String transactionRefId) {

		if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
			declinedTransactionLogger.saveDeclinedTransaction(senderId, receiverId,

					amount != null ? amount : BigDecimal.ZERO, type, "Amount must be greater than zero.",
					transactionRefId);
			throw new BadRequestException("Amount must be greater than zero.");

		}
	}

	public Account getActiveAccountAuthById(Long id, Long userId, TransactionType type, BigDecimal amount,
			Account sender, Account receiver, String transactionRefId) {
		try {
			Account account = accountRepository.findById(id)
					.orElseThrow(() -> new NotFoundException("Account not found"));

			if (!account.getUserId().equals(userId)) {
				throw new ForbiddenException("Unauthorized access to this account");
			}

			if (account.getStatus() != AccountStatus.ACTIVE) {
				throw new BadRequestException("Account is inactive or frozen");
			}

			return account;

		} catch (RuntimeException e) {
			if (e instanceof BadRequestException || e instanceof NotFoundException || e instanceof ForbiddenException) {
				declinedTransactionLogger.saveDeclinedTransaction(sender != null ? sender.getId() : id,
						receiver != null ? receiver.getId() : id, amount != null ? amount : BigDecimal.ZERO, type,
						e.getMessage(), transactionRefId);
			}
			throw e;
		}

	}

	public Account getGlobalAccount() {
		return accountRepository.findByIsGlobalAccountTrue()
				.orElseThrow(() -> new NotFoundException("Global account not found."));
	}

	public String getNarrationOrDefault(String narration, TransactionType type) {
		return (narration != null && !narration.trim().isEmpty()) ? narration : "Auto-narration for " + type.name();
	}

	public String generateTransactionReferenceId() {
		return "TXN-" + System.currentTimeMillis() + "-" + (int) (Math.random() * 10000);
		// return "TXN-" + UUID.randomUUID();
	}

	public void validateCard(Long senderaccountId, Long receiverId, String transactionRefId, String cardNumber,
			String cardPin, AccountStatus status) {

		if (senderaccountId == null) {
			throw new BadRequestException("Account ID is required for card verification.");
		}
		if (transactionRefId == null || transactionRefId.isBlank()) {
			throw new BadRequestException("Transaction reference ID is required.");
		}
		if (cardNumber == null || cardNumber.isBlank()) {
			throw new BadRequestException("Card number is required.");
		}
		if (cardPin == null || cardPin.isBlank()) {
			throw new BadRequestException("Card PIN is required.");
		}

		// String cardServiceUrl =
		// "http://192.168.100.146:8081/api/v1/cards/internal/verify";
		CardVerificationRequest request = new CardVerificationRequest(senderaccountId, transactionRefId, cardNumber,
				cardPin, status);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<CardVerificationRequest> entity = new HttpEntity<>(request, headers);

		Boolean isValid;
		try {
			isValid = restTemplate.postForObject(cardServiceUrl, entity, Boolean.class);
		} catch (Exception ex) {
			log.error("Card verification failed due to service error", ex);
			throw new BadRequestException("Card verification failed. Please try again later.");
		}

		if (Boolean.FALSE.equals(isValid)) {

			declinedTransactionLogger.saveDeclinedTransaction(senderaccountId, receiverId, BigDecimal.ZERO,
					TransactionType.PURCHASE, "Invalid card credentials or transaction reference.", transactionRefId);
			throw new BadRequestException("Invalid card credentials or transaction reference.");
		}
	}

	public void validateCardWithCvv(Long senderaccountId, Long receiverId, String transactionRefId, String cardNumber,
			String cvv, AccountStatus status) {

		if (senderaccountId == null) {
			throw new BadRequestException("Account ID is required for card verification.");
		}
		if (transactionRefId == null || transactionRefId.isBlank()) {
			throw new BadRequestException("Transaction reference ID is required.");
		}
		if (cardNumber == null || cardNumber.isBlank()) {
			throw new BadRequestException("Card number is required.");
		}
		if (cvv == null || cvv.isBlank()) {
			throw new BadRequestException("CVV is required.");
		}

		CardVerificationRequest request = new CardVerificationRequest(senderaccountId, transactionRefId, cardNumber,
				cvv, status);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<CardVerificationRequest> entity = new HttpEntity<>(request, headers);

		Boolean isValid;
		try {
			isValid = restTemplate.postForObject(cardServiceUrl, entity, Boolean.class);
		} catch (Exception ex) {
			log.error("Card verification failed due to service error", ex);
			throw new BadRequestException("Card verification failed. Please try again later.");
		}

		if (Boolean.FALSE.equals(isValid)) {

			declinedTransactionLogger.saveDeclinedTransaction(senderaccountId, receiverId, BigDecimal.ZERO,
					TransactionType.PURCHASE, "Invalid card credentials or transaction reference.", transactionRefId);
			throw new BadRequestException("Invalid card credentials or transaction reference.");
		}
	}

	public void validateGlobalAccountConstraints(TransactionType type, Account sender, Account receiver,
			BigDecimal amount, String transactionRefId) {
		String message = null;

		switch (type) {
		case DEPOSIT:
			if (receiver.isGlobalAccount()) {
				message = "ACCESS DENIED-Receiver cannot be the Global Account in a deposit.";
			}
			break;

		case WITHDRAW:
		case PURCHASE:
			if (sender.isGlobalAccount()) {
				message = "ACCESS DENIED-Sender cannot be the Global Account  ";
			}
			break;

		case TRANSFER:
			if (sender.isGlobalAccount() || receiver.isGlobalAccount()) {
				message = "Global Account cannot be involved in a user-to-user transfer.";
			}
			break;

		default:
			break;
		}

		if (message != null) {
			declinedTransactionLogger.saveDeclinedTransaction(sender.getId(), receiver.getId(), amount, type, message,
					transactionRefId);
			throw new BadRequestException(message);
		}
	}

	public void validateTransactionRefId(String transactionRefId, Long senderId, Long receiverId, BigDecimal amount,
			TransactionType type) {
		if (transactionRefId == null || transactionRefId.isBlank()) {
			declinedTransactionLogger.saveDeclinedTransaction(senderId, receiverId, amount, type,
					"Missing transaction reference ID", transactionRefId);
			throw new BadRequestException("Transaction reference ID is required.");
		}

		if (paymentRepository.existsByTransactionRefId(transactionRefId)) {
			declinedTransactionLogger.saveDeclinedTransaction(senderId, receiverId, amount, type,
					"Transaction reference ID already exists", transactionRefId

			);
			throw new BadRequestException(
					"Transaction with reference ID already exists. Duplicate transaction not allowed.");
		}
	}

	public TransactionHistoryDTO toDTO(Payment p, Account sender, Account receiver) {
		TransactionHistoryDTO dto = new TransactionHistoryDTO();
		dto.setAmount(p.getAmount());
		dto.setId(p.getId());
		dto.setType(p.getType());
		dto.setStatus(p.getStatus());
		dto.setTransactionRefId(p.getTransactionRefId());
		dto.setNarration(p.getNarration());
		dto.setCreatedAt(p.getCreatedAt());

		if (sender != null && !sender.isGlobalAccount()) {
			dto.setSenderAccount(sender);
		}
		if (receiver != null && !receiver.isGlobalAccount()) {
			dto.setReceiverAccount(receiver);
		}
		return dto;
	}

	public void validateSufficientBalance(Account sender, Account receiver, BigDecimal amount, TransactionType type,
			String transactionRefId) {

		if (sender.getBalance().compareTo(amount) < 0) {
			declinedTransactionLogger.saveDeclinedTransaction(sender.getId(), receiver.getId(), amount, type,
					"Insufficient balance", transactionRefId

			);
			throw new BadRequestException("Insufficient balance");

		}
	}

	public TransactionHistoryResponseDTO toHistoryResponse(Payment payment, Account sender, Account receiver,
			boolean isAdmin) {
		TransactionHistoryResponseDTO.TransactionHistoryResponseDTOBuilder builder = TransactionHistoryResponseDTO
				.builder().id(payment.getId()).amount(payment.getAmount()).type(payment.getType())
				.status(payment.getStatus()).narration(payment.getNarration())
				.transactionRefId(payment.getTransactionRefId()).createdAt(payment.getCreatedAt());

		if (sender != null && (!sender.isGlobalAccount() || isAdmin)) {
			builder.senderAccount(new AccountInfoDTO(sender));
		}

		if (receiver != null && (!receiver.isGlobalAccount() || isAdmin)) {
			builder.receiverAccount(new AccountInfoDTO(receiver));
		}

		return builder.build();
	}

}