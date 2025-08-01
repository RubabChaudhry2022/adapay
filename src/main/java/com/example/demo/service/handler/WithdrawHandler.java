package com.example.demo.service.handler;

import java.math.BigDecimal;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import com.example.demo.dto.WithdrawRequest;
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
public class WithdrawHandler {

	private final AccountRepository accountRepository;
	private final TransactionUtils transactionUtils;
	private WithdrawRequest request;
	private Account sender;
	private Account receiver;
	private Long senderId;
	private Long receiverId;
	private String transactionRefId;
	public TransactionHistoryDTO handle(WithdrawRequest request, Long userId) {

		initializer(request, userId);
		validator(senderId, request.getAmount(), transactionRefId, receiverId);
		updator(request.getAmount());
		return logger(request, userId);
	}

	public void initializer(WithdrawRequest request, Long userId) {
		this.request = request;
		this.transactionRefId = request.getTransactionRefId();
		this.senderId = request.getAccountId();
		this.receiverId = transactionUtils.getGlobalAccount().getId();
		this.receiver = transactionUtils.getGlobalAccount();

		this.sender = transactionUtils.getActiveAccountAuthById(senderId, userId, TransactionType.WITHDRAW,
				request.getAmount(), transactionUtils.getAccountOrNull(senderId), receiver, transactionRefId);

	}

	public void validator(Long senderId, BigDecimal amount, String transactionRefId, Long receiverId) {
		transactionUtils.validateTransactionRefId(transactionRefId, senderId, receiverId, amount,
				TransactionType.WITHDRAW);

		transactionUtils.validateCard(senderId, receiverId, transactionRefId, request.getCardNumber(),
				request.getCardPin(), sender.getStatus());

		transactionUtils.validateAmount(amount, TransactionType.WITHDRAW, senderId, receiverId, transactionRefId);
		transactionUtils.validateGlobalAccountConstraints(TransactionType.WITHDRAW, sender, receiver, amount,
				transactionRefId);
		transactionUtils.validateMaxAmount(amount, TransactionType.WITHDRAW, sender, receiver, transactionRefId);
		transactionUtils.validateSufficientBalance(sender, receiver, amount, TransactionType.WITHDRAW,
				transactionRefId);
	}

	public void updator(BigDecimal amount) {
		sender.setBalance(sender.getBalance().subtract(amount));
		receiver.setBalance(receiver.getBalance().add(amount));
		accountRepository.save(sender);
		accountRepository.save(receiver);
	}

	public TransactionHistoryDTO logger(WithdrawRequest request, Long userId) {
		Payment transaction = transactionUtils.saveTransaction(request.getAmount(), TransactionType.WITHDRAW,
				TransactionStatus.SUCCESS,
				transactionUtils.getNarrationOrDefault(request.getNarration(), TransactionType.WITHDRAW),
				transactionRefId, sender.getId(), receiver.getId(), sender, receiver);

		transactionUtils.saveTransaction(request.getAmount(), TransactionType.WITHDRAW, TransactionStatus.SUCCESS,
				"Global ledger - "
						+ transactionUtils.getNarrationOrDefault(request.getNarration(), TransactionType.WITHDRAW),
				"GL- " + transactionRefId, sender.getId(), receiver.getId(), sender, receiver);

		return transactionUtils.toDTO(transaction, sender, null);
	}
}
