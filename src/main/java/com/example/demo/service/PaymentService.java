package com.example.demo.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.Account;
import com.example.demo.model.Payment;
import com.example.demo.repository.PaymentRepository;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private AccountService accountService;  

    public Payment depositMoney(Long userId, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than 0");
        }

       
        Account account = accountService.getAccountByUserId(userId);

        if (account == null) {
            throw new RuntimeException("Account not found for user ID: " + userId);
        }
        account.setBalance(account.getBalance().add(amount));

        accountService.updateAccount(account); 

        Payment payment = new Payment(
            null, null, null,
            amount,
            "SUCCESS",
            "DEPOSIT",
            LocalDateTime.now(),
            userId
        );

        return paymentRepository.save(payment);
    }
}
