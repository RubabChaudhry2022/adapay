package com.example.demo.service.handler;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import com.example.demo.dto.PaginatedResponse;
import com.example.demo.dto.TransactionHistoryResponseDTO;
import com.example.demo.dto.UserDto;
import com.example.demo.enums.AccountStatus;
import com.example.demo.model.Account;
import com.example.demo.model.Payment;
import com.example.demo.repository.AccountRepository;
import com.example.demo.repository.PaymentRepository;
import com.example.demo.service.helper.TransactionUtils;
import com.example.demo.util.JwtUtil;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TransactionHistoryHandler {

	private final PaymentRepository paymentRepository;
	private final AccountRepository accountRepository;
	private final TransactionUtils transactionUtils;
	private final JwtUtil jwtUtil;

	public PaginatedResponse<TransactionHistoryResponseDTO> getTransactionHistory(Long accountId, String token,
			int page, int size) {
		int pageNumber = (page > 0) ? page - 1 : 0;
		Pageable pageable = PageRequest.of(pageNumber, size, Sort.by(Sort.Direction.DESC, "createdAt"));

		UserDto user;
		try {
			user = jwtUtil.extractUser(token);
		} catch (Exception ex) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or expired token");
		}

		boolean isAdmin = "ADMIN".equalsIgnoreCase(user.getRole());

		Page<Payment> paymentsPage;

		if (accountId == null) {
			if (!isAdmin) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Users must provide accountId.");
			}
			paymentsPage = paymentRepository.findAll(pageable);

			if (pageNumber >= paymentsPage.getTotalPages() && paymentsPage.getTotalPages() > 0) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Page number exceeds total pages.");
			}
		} else {

			Account requestedAccount = accountRepository.findById(accountId)
					.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found"));

			if (!isAdmin && !requestedAccount.getUserId().equals(user.getId())) {
				throw new ResponseStatusException(HttpStatus.FORBIDDEN,
						"You are not authorized to view this account's transactions.");

			}
			if (requestedAccount.getStatus() == AccountStatus.BLOCKED) {
				throw new ResponseStatusException(HttpStatus.FORBIDDEN,
						"Your account is blocked. Transaction history access denied.");
			}

			paymentsPage = paymentRepository.findByAccountInvolved(accountId, pageable);
			if (pageNumber >= paymentsPage.getTotalPages() && paymentsPage.getTotalPages() > 0) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Page number exceeds total pages.");
			}
		}

		List<Payment> filteredPayments;

		if (!isAdmin || accountId != null) {
			filteredPayments = paymentsPage.getContent().stream().filter(p -> {
				Long senderId = p.getSenderAccountId();
				Long receiverId = p.getReceiverAccountId();

				boolean isSenderGA = transactionUtils.isGlobalAccountSafe(senderId);
				boolean isReceiverGA = transactionUtils.isGlobalAccountSafe(receiverId);

				boolean isUserSender = senderId != null && senderId.equals(accountId);
				boolean isUserReceiver = receiverId != null && receiverId.equals(accountId);

				if (isSenderGA && isUserReceiver && !isReceiverGA)
					return true;

				if (isUserSender && isReceiverGA && !isSenderGA)
					return true;

				if ((isUserSender || isUserReceiver) && !isSenderGA && !isReceiverGA)
					return true;

				return false; // hide GA
			}).toList();
		} else {
			filteredPayments = paymentsPage.getContent();
		}

		List<TransactionHistoryResponseDTO> filteredDtos = paymentsPage.getContent().stream().filter(p -> {
			Long senderId = p.getSenderAccountId();
			Long receiverId = p.getReceiverAccountId();

			boolean isSenderGA = transactionUtils.isGlobalAccountSafe(senderId);
			boolean isReceiverGA = transactionUtils.isGlobalAccountSafe(receiverId);

			boolean isUserSender = senderId != null && senderId.equals(accountId);
			boolean isUserReceiver = receiverId != null && receiverId.equals(accountId);

			if (isAdmin && accountId == null)
				return true;

			if (isAdmin && accountId != null) {
				return isUserSender || isUserReceiver;
			}

			if (!isAdmin) {
				if (p.getTransactionRefId().startsWith("GL-"))
					return false;

				return (isUserSender && !isSenderGA) || (isUserReceiver && !isReceiverGA);
			}

			return false;
		}).map(p -> transactionUtils.toHistoryResponse(p, transactionUtils.safeFindAccount(p.getSenderAccountId()),
				transactionUtils.safeFindAccount(p.getReceiverAccountId()), isAdmin)).toList();
		return new PaginatedResponse<>(filteredDtos, paymentsPage.getNumber() + 1, paymentsPage.getSize(),
				paymentsPage.getTotalElements(), paymentsPage.getTotalPages(), paymentsPage.isLast());

	}

}