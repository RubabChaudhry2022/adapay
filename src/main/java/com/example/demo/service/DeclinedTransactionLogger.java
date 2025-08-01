package com.example.demo.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.example.demo.enums.TransactionStatus;
import com.example.demo.enums.TransactionType;
import com.example.demo.model.Account;
import com.example.demo.model.Payment;
import com.example.demo.repository.PaymentRepository;
import org.springframework.transaction.annotation.Propagation;

import org.springframework.transaction.annotation.Transactional;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class DeclinedTransactionLogger {

	private PaymentRepository paymentRepository;
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void saveDeclinedTransaction(
	    Long senderId,
	    Long receiverId,
	    BigDecimal amount,
	    TransactionType type,
	    String narration,
	    String transactionRefId  
	) {
	    Payment payment = new Payment();
	    payment.setSenderAccountId(senderId);
	    payment.setReceiverAccountId(receiverId);
	    payment.setAmount(amount != null ? amount : BigDecimal.ZERO);
	    payment.setType(type);
	    payment.setStatus(TransactionStatus.DECLINED);
	    payment.setNarration(narration);
	  // payment.setTransactionRefId(   (transactionRefId != null && !transactionRefId.isBlank()) ? transactionRefId : "TXN-" + System.currentTimeMillis()  );
	//payment.setTransactionRefId(transactionRefId);
	      String refToSave;
	    if (transactionRefId == null || transactionRefId.isBlank()) {
	        refToSave = "DECLINED-" + System.currentTimeMillis();
	    } else if (paymentRepository.existsByTransactionRefId(transactionRefId)) {
	        refToSave ="DECLINED-"+ transactionRefId ;
	    } else {
	        refToSave = transactionRefId;
	    }
	    payment.setTransactionRefId(refToSave);

	    payment.setCreatedAt(LocalDateTime.now());

	    paymentRepository.save(payment);
	}
	/*
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void saveDeclinedTransaction(BigDecimal amount, TransactionType type, String narration, String referenceId,
			Account sender, Account receiver) {
		Payment payment = new Payment();
		payment.setAmount(amount);
		payment.setType(type); 
		payment.setStatus(TransactionStatus.DECLINED); 
		payment.setNarration(narration);
		payment.setTransactionRefId(referenceId); 
		payment.setCreatedAt(LocalDateTime.now()); 

		if (sender != null)
			payment.setSenderAccountId(sender.getId());
		if (receiver != null)
			payment.setReceiverAccountId(receiver.getId());

		paymentRepository.save(payment);
	}
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void saveDeclinedTransaction(Long senderId, Long receiverId, BigDecimal amount, TransactionType type, String reason) {
	    Payment payment = new Payment();
	    payment.setSenderAccountId(senderId);
	    payment.setReceiverAccountId(receiverId);
	    payment.setAmount(amount);
	    payment.setType(type);
	    payment.setStatus(TransactionStatus.DECLINED);
	    payment.setNarration(reason);
	    payment.setTransactionRefId("TXN-" + System.currentTimeMillis());
	    payment.setCreatedAt(LocalDateTime.now());

	    paymentRepository.save(payment);
	}
	*/



}
