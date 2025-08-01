package com.example.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.example.demo.dto.DepositRequest;
import com.example.demo.dto.PaginatedResponse;
import com.example.demo.dto.PurchaseRequest;
import com.example.demo.dto.TransactionHistoryDTO;
import com.example.demo.dto.TransactionHistoryResponseDTO;
import com.example.demo.dto.TransferRequest;
import com.example.demo.dto.TransferResponseDTO;
import com.example.demo.dto.WithdrawRequest;
import com.example.demo.service.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/payment")
@AllArgsConstructor
@Validated
public class PaymentController {

	private final PaymentService paymentService;

	@PostMapping("/deposit")
	public ResponseEntity<TransactionHistoryDTO> deposit(@Valid @RequestBody DepositRequest request,
			HttpServletRequest httpRequest) {
		Long userId = (Long) httpRequest.getAttribute("userId");
		TransactionHistoryDTO response = paymentService.deposit(request, userId);
		return ResponseEntity.ok(response);
	}

	@PostMapping("/withdraw")
	public ResponseEntity<TransactionHistoryDTO> withdraw(@Valid @RequestBody WithdrawRequest request,
			HttpServletRequest httpRequest) {
		Long userId = (Long) httpRequest.getAttribute("userId");
		TransactionHistoryDTO response = paymentService.withdraw(request, userId);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/history")
	public PaginatedResponse<TransactionHistoryResponseDTO> getAllTransactionHistory(
			@RequestHeader("Authorization") String token, @RequestParam(defaultValue = "1") int page,
			@RequestParam(defaultValue = "10") int size, @RequestParam(required = false) Long accountId) {

		if (token != null && token.startsWith("Bearer ")) {
			token = token.substring(7);
		}

		return paymentService.getTransactionHistory(accountId, token, page, size);
	}

	@PostMapping("/transfer")
	public ResponseEntity<TransferResponseDTO> transfer(@RequestBody TransferRequest request,
			HttpServletRequest httpRequest) {
		Long userId = (Long) httpRequest.getAttribute("userId");
		TransferResponseDTO response = paymentService.transfer(request, userId);
		return ResponseEntity.ok(response);
	}

	@PostMapping("/purchase")
	public ResponseEntity<TransactionHistoryDTO> purchase(@Valid @RequestBody PurchaseRequest request,
			HttpServletRequest httpRequest) {
		Long userId = (Long) httpRequest.getAttribute("userId");
		TransactionHistoryDTO response = paymentService.purchase(request, userId);
		return ResponseEntity.ok(response);
	}

}
