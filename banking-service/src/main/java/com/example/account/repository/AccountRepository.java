package com.example.account.repository;
import com.example.account.model.AccountModel;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<AccountModel, Long> {
    Optional<AccountModel> findByUserId(Long userId);
    Optional<AccountModel> findByAccountNumber(String accountNumber);
}
