package com.example.demo.service.handler;

import java.math.BigDecimal;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import com.example.demo.dto.TransferRequest;
import com.example.demo.dto.TransferResponseDTO;
import com.example.demo.enums.AccountStatus;
import com.example.demo.enums.TransactionStatus;
import com.example.demo.enums.TransactionType;
import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.NotFoundException;
import com.example.demo.model.Account;
import com.example.demo.model.Payment;
import com.example.demo.repository.AccountRepository;
import com.example.demo.service.DeclinedTransactionLogger;
import com.example.demo.service.helper.TransactionUtils;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@Scope("prototype")
public class TransferHandler {

	private final AccountRepository accountRepository;
	private final TransactionUtils transactionUtils;
	private final DeclinedTransactionLogger declinedTransactionLogger;
	private TransferRequest request;
	private Account sender;
	private Account receiver;
	private Long senderId;
	private Long receiverId;
	private String transactionRefId;

	public TransferResponseDTO handle(TransferRequest request, Long userId) {
		initializer(request, userId);
		validator(request.getAmount());
		updator(request.getAmount());
		return logger(request);
	}

	public void initializer(TransferRequest request, Long userId) {
		this.request = request;
		this.transactionRefId = transactionUtils.generateTransactionReferenceId();
		this.senderId = request.getSenderAccountId();
		this.receiverId = request.getReceiverAccountId();
		try {
			this.sender = accountRepository.findById(senderId)
					.orElseThrow(() -> new NotFoundException("Sender account not found"));

			if (sender.getStatus() != AccountStatus.ACTIVE) {
				throw new BadRequestException("Sender account is not active");
			}
			if (!sender.getUserId().equals(userId)) {
				declinedTransactionLogger.saveDeclinedTransaction(senderId, receiverId, request.getAmount(),
						TransactionType.TRANSFER, "Sender account does not belong to the logged-in user",
						transactionRefId);
				throw new BadRequestException("You are not authorized to transfer from this account.");
			}

		} catch (RuntimeException e) {
			declinedTransactionLogger.saveDeclinedTransaction(senderId, receiverId, request.getAmount(),
					TransactionType.TRANSFER, e.getMessage(), transactionRefId);
			throw e;
		}

		try {
			this.receiver = accountRepository.findById(receiverId)
					.orElseThrow(() -> new NotFoundException("Receiver account not found"));
//"Receiver account not found with ID: " + request.getReceiverAccountId()
			if (receiver.getStatus() != AccountStatus.ACTIVE) {
				throw new BadRequestException("Receiver account is not active");
			}

		} catch (RuntimeException e) {
			declinedTransactionLogger.saveDeclinedTransaction(senderId, receiverId, request.getAmount(),
					TransactionType.TRANSFER, e.getMessage(), transactionRefId);
			throw e;
		}

	}

	public void validator(BigDecimal amount) {

		transactionUtils.validateAmount(amount, TransactionType.TRANSFER, senderId, receiverId, transactionRefId);
		if (request.getSenderAccountId().equals(request.getReceiverAccountId())) {
			declinedTransactionLogger.saveDeclinedTransaction(request.getSenderAccountId(),
					request.getReceiverAccountId(), request.getAmount(), TransactionType.TRANSFER,
					"Same Sender and Receiver id", transactionRefId

			);
			throw new BadRequestException("Sender and receiver cannot be the same.");
		}
		transactionUtils.validateGlobalAccountConstraints(TransactionType.TRANSFER, sender, receiver, amount,
				transactionRefId);
		transactionUtils.validateMaxAmount(amount, TransactionType.TRANSFER, sender, receiver, transactionRefId);
		transactionUtils.validateSufficientBalance(sender, receiver, amount, TransactionType.TRANSFER,
				transactionRefId);
	}

	public void updator(BigDecimal amount) {
		sender.setBalance(sender.getBalance().subtract(amount));
		receiver.setBalance(receiver.getBalance().add(amount));
		accountRepository.save(sender);
		accountRepository.save(receiver);
	}

	public TransferResponseDTO logger(TransferRequest request) {
		Payment senderTxn = transactionUtils.saveTransaction(request.getAmount(), TransactionType.TRANSFER,
				TransactionStatus.SUCCESS,
				transactionUtils.getNarrationOrDefault(request.getNarration(), TransactionType.TRANSFER),
				transactionRefId, sender.getId(), receiver.getId(), sender, receiver);

		Payment receiverTxn = transactionUtils.saveTransaction(request.getAmount(), TransactionType.TRANSFER,
				TransactionStatus.SUCCESS,
				"R- " + transactionUtils.getNarrationOrDefault(request.getNarration(), TransactionType.TRANSFER),
				"R- " + transactionRefId, sender.getId(), receiver.getId(), sender, receiver);

		TransferResponseDTO response = new TransferResponseDTO();
		response.setReferenceId(transactionRefId);
		response.setSenderTransaction(transactionUtils.toDTO(senderTxn, sender, receiver));
		response.setReceiverTransaction(transactionUtils.toDTO(receiverTxn, sender, receiver));

		return response;
	}
}
