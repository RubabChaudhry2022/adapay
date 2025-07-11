package com.example.demo.controller;
import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.Payment;
import com.example.demo.service.PaymentService;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

	
    @Autowired
    private PaymentService paymentService;

    @PostMapping("/deposit")
    public ResponseEntity<Payment> deposit(
            @RequestParam Long userId,
            @RequestParam BigDecimal amount) {

        Payment payment = paymentService.depositMoney(userId, amount);
        return ResponseEntity.ok(payment);
    }

}
