package com.example.demo.controller;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.AccountDto;
import com.example.demo.dto.AccountSummaryDto;
import com.example.demo.dto.UserDto;
import com.example.demo.enums.Currency;
import com.example.demo.mapper.AccountMapper;
import com.example.demo.service.AccountService;
import com.example.demo.util.ApiResponse;
import com.example.demo.util.JwtUtil;
import com.example.demo.util.PaginationResponse;
import com.example.demo.util.Token;
import com.example.demo.util.TokenUtil;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
public class AccountController {

	private final AccountService accountService;
	private final JwtUtil jwtUtil;
	private final Token tokenService;

	 @PostMapping
	 public ResponseEntity<AccountDto> createAccount(
	         @RequestHeader("Authorization") String token
	 ) {
	     if (!isValidToken(token)) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	     String jwt = token.substring(7);
	     return new ResponseEntity<>(accountService.createAccountFromToken(jwt), HttpStatus.CREATED);
	 }

	    private boolean isValidToken(String token) {
	        return token != null && token.startsWith("Bearer ");
	    }

	@GetMapping
    public ResponseEntity<ApiResponse<PaginationResponse<AccountDto>>> getAccounts(
            @RequestHeader("Authorization") String token,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        String jwt = TokenUtil.extractJwt(token);
        Pageable pageable = PageRequest.of(page, size);
        PaginationResponse<AccountDto> response = accountService.getAccountsBasedOnRole(jwt, pageable);

        return ResponseEntity.ok(ApiResponse.success("Accounts fetched successfully", response));
    }

    @GetMapping("/{accountId}")
    public ResponseEntity<ApiResponse<AccountDto>> getAccountById(
            @PathVariable Long accountId,
            @RequestHeader("Authorization") String token) {

        String jwt = TokenUtil.extractJwt(token);
        AccountDto account = accountService.getAccountByIdForUser(accountId, jwt);
        return ResponseEntity.ok(ApiResponse.success("Account fetched successfully", account));
    }

    @PutMapping("/{accountId}")
    public ResponseEntity<ApiResponse<AccountDto>> updateAccountStatus(
            @PathVariable Long accountId,
            @Valid @RequestBody AccountDto accountDTO,
            @RequestHeader("Authorization") String token) {

        String jwt = TokenUtil.extractJwt(token);
        AccountDto updatedAccount = accountService.updateAccountStatus(accountId, accountDTO, jwt);
        return ResponseEntity.ok(ApiResponse.success("Account status updated successfully", updatedAccount));
    }

    @GetMapping("/by-token")
    public ResponseEntity<ApiResponse<AccountSummaryDto>> getAccountByToken(
            @RequestHeader("Authorization") String token,
            @RequestParam(value = "currency", required = false) Currency currency) {

        String jwt = TokenUtil.extractJwt(token);
        UserDto user = jwtUtil.extractUser(jwt);
        Currency effectiveCurrency = (currency != null) ? currency : Currency.PKR;

        AccountDto account = accountService.getAccountByUserIdAndCurrency(user.getId(), effectiveCurrency);
        AccountSummaryDto summary = AccountMapper.toSummary(account);

        return ResponseEntity.ok(ApiResponse.success("Account summary fetched successfully", summary));
    }




	

}
