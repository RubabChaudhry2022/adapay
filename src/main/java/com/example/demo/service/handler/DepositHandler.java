package com.example.demo.service.handler;

import java.math.BigDecimal;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import com.example.demo.dto.DepositRequest;
import com.example.demo.dto.TransactionHistoryDTO;
import com.example.demo.enums.TransactionStatus;
import com.example.demo.enums.TransactionType;
import com.example.demo.model.Account;
import com.example.demo.model.Payment;
import com.example.demo.repository.AccountRepository;
import com.example.demo.service.helper.TransactionUtils;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@Scope("prototype")
public class DepositHandler {

	private final AccountRepository accountRepository;
	private final TransactionUtils transactionUtils;

	private DepositRequest request;
	private Account sender;
	private Account receiver;
	private Long senderId;
	private Long receiverId;
	private String transactionRefId;

	public TransactionHistoryDTO handle(DepositRequest request, Long userId) {

		initializer(request, userId);
		validator(senderId, request.getAmount(), transactionRefId, receiverId);
		updator(request.getAmount());
		return logger(request, userId);
	}

	public void initializer(DepositRequest request, Long userId) {
		this.request = request;
		transactionRefId = transactionUtils.generateTransactionReferenceId();
		System.out.println("REFID" + transactionRefId);
		this.receiverId = request.getAccountId();
		this.senderId = transactionUtils.getGlobalAccount().getId();
		this.sender = transactionUtils.getGlobalAccount();
		this.receiver = transactionUtils.getActiveAccountAuthById(request.getAccountId(), userId,
				TransactionType.DEPOSIT, request.getAmount(), sender,
				transactionUtils.getAccountOrNull(request.getAccountId()), transactionRefId);

	}

	public void validator(Long senderId, BigDecimal amount, String transactionRefId, Long receiverId) {
		transactionUtils.validateAmount(amount, TransactionType.DEPOSIT, senderId, receiverId, transactionRefId);
		transactionUtils.validateGlobalAccountConstraints(TransactionType.DEPOSIT, sender, receiver, amount,
				transactionRefId);
		transactionUtils.validateMaxAmount(amount, TransactionType.DEPOSIT, sender, receiver, transactionRefId);
	}

	public void updator(BigDecimal amount) {
		receiver.setBalance(receiver.getBalance().add(amount));
		sender.setBalance(sender.getBalance().subtract(amount));
		accountRepository.save(receiver);
		accountRepository.save(sender);
	}

	public TransactionHistoryDTO logger(DepositRequest request, Long userId) {

		Payment transaction = transactionUtils.saveTransaction(request.getAmount(), TransactionType.DEPOSIT,
				TransactionStatus.SUCCESS,
				transactionUtils.getNarrationOrDefault(request.getNarration(), TransactionType.DEPOSIT),
				transactionRefId, sender.getId(), receiver.getId(), sender, receiver);

		transactionUtils.saveTransaction(request.getAmount(), TransactionType.DEPOSIT, TransactionStatus.SUCCESS,
				"Global ledger - "
						+ transactionUtils.getNarrationOrDefault(request.getNarration(), TransactionType.DEPOSIT),
				"GL- " + transactionRefId, sender.getId(), receiver.getId(), sender, receiver);

		return transactionUtils.toDTO(transaction, null, receiver);
	}
}