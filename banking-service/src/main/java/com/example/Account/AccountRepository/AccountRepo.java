package com.example.Account.AccountRepository;

import com.example.Account.AccountModel.AccountModel;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AccountRepo extends JpaRepository<AccountModel, Long> {
    Optional<AccountModel> findByUserId(Long userId);
}
