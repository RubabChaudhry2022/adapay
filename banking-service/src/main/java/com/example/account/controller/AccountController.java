package com.example.account.controller;
import com.example.account.dto.AccountDto;
import com.example.account.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping
    public ResponseEntity<AccountDto> createAccount(@Valid @RequestBody AccountDto accountDTO) {
        AccountDto created = accountService.createAccount(accountDTO);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<AccountDto>> getAllAccounts() {
        return ResponseEntity.ok(accountService.getAllAccounts());
    }

    
    @GetMapping("/{userId}")
    public ResponseEntity<AccountDto> getAccountByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(accountService.getAccountByUserId(userId));
    }

   
    @PutMapping("/{userId}")
    public ResponseEntity<AccountDto> updateAccount(@PathVariable Long userId, @Valid @RequestBody AccountDto accountDTO) {
        return ResponseEntity.ok(accountService.updateAccount(userId, accountDTO));
    }

   
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteAccount(@PathVariable Long userId) {
        accountService.deleteAccount(userId);
        return ResponseEntity.noContent().build();
    }
}
