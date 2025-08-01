package com.example.demo.service;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.demo.dto.DepositRequest;
import com.example.demo.dto.PaginatedResponse;
import com.example.demo.dto.PurchaseRequest;
import com.example.demo.dto.TransactionHistoryDTO;
import com.example.demo.dto.TransactionHistoryResponseDTO;
import com.example.demo.dto.TransferRequest;
import com.example.demo.dto.TransferResponseDTO;
import com.example.demo.dto.WithdrawRequest;
import com.example.demo.service.handler.DepositHandler;
import com.example.demo.service.handler.PurchaseHandler;
import com.example.demo.service.handler.TransactionHistoryHandler;
import com.example.demo.service.handler.TransferHandler;
import com.example.demo.service.handler.WithdrawHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

	//private final PurchaseHandler purchaseHandler;
	//private final TransferHandler transferHandler;
	private final TransactionHistoryHandler transactionHistoryHandler;
	private final ApplicationContext applicationContext;

	@Transactional
	public TransactionHistoryDTO deposit(DepositRequest request, Long userId) {

		DepositHandler depositHandler = applicationContext.getBean(DepositHandler.class);
		return depositHandler.handle(request, userId);

	}

	@Transactional
	public TransactionHistoryDTO withdraw(WithdrawRequest request, Long userId) {
		WithdrawHandler withdrawHandler = applicationContext.getBean(WithdrawHandler.class);
		return withdrawHandler.handle(request, userId);

	}

	@Transactional
	public TransactionHistoryDTO purchase(PurchaseRequest request, Long userId) {
		PurchaseHandler purchaseHandler=applicationContext.getBean(PurchaseHandler.class);
		return purchaseHandler.handle(request, userId);

	}

	@Transactional
	public TransferResponseDTO transfer(TransferRequest request, Long userId) {
		TransferHandler transferHandler=applicationContext.getBean(TransferHandler.class);
		return transferHandler.handle(request, userId);

	}

	public PaginatedResponse<TransactionHistoryResponseDTO> getTransactionHistory(Long accountId, String token,
			int page, int size) {
		return transactionHistoryHandler.getTransactionHistory(accountId, token, page, size);

	}

}
