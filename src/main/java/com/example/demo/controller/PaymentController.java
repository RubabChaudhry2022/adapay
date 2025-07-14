package com.example.demo.controller;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.example.demo.dto.DepositRequest;
import com.example.demo.dto.TransactionHistoryDTO;
import com.example.demo.dto.TransferRequest;
import com.example.demo.dto.WithdrawRequest;
import com.example.demo.model.Payment;
import com.example.demo.service.PaymentService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/payment")
@AllArgsConstructor
public class PaymentController {

	private final PaymentService paymentService;

	@PostMapping("/deposit")
	public ResponseEntity<Payment> deposit(@Valid @RequestBody DepositRequest request) {
		Payment payment = paymentService.depositMoney(request);
		return ResponseEntity.ok(payment);
	}

	@PostMapping("/withdraw")
	public ResponseEntity<Payment> withdraw(@Valid @RequestBody WithdrawRequest request) {
		Payment payment = paymentService.withdrawMoney(request);
		return ResponseEntity.ok(payment);
	}

	@GetMapping("/history")
	public ResponseEntity<List<TransactionHistoryDTO>> getTransactionHistory(@RequestParam Long userId) {
		List<TransactionHistoryDTO> history = paymentService.getTransactionHistory(userId);
		return ResponseEntity.ok(history);
	}

	@PostMapping("/transfer")
	public ResponseEntity<String> transferMoney(@Valid @RequestBody TransferRequest request) {
		String payment = paymentService.transferMoney(request);
		return ResponseEntity.ok(payment);
	}
}
