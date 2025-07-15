package com.example.Account;

import com.example.Account.AccountModel.AccountModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    @Autowired
    private AccountService accountService;

    
    @GetMapping
    public List<AccountModel> getAllAccounts() {
        return accountService.getAllAccounts();
    }

    
    @GetMapping("/{userId}")
    public AccountModel getAccountByUserId(@PathVariable Long userId) {
        return accountService.getAccountByUserId(userId);
    }
}
