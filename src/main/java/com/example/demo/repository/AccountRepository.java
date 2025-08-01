package com.example.demo.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.enums.AccountStatus;
import com.example.demo.enums.Currency;
import com.example.demo.model.Account;

public interface AccountRepository extends JpaRepository<Account, Long> {
	Optional<Account> findByUserId(Long userId);

	Optional<Account> findByAccountNumber(String accountNumber);

	Optional<Account> findByIdAndStatus(Long id, AccountStatus status);

	Optional<Account> findByIsGlobalAccountTrue();

	boolean existsByUserIdAndCurrency(Long userId, Currency currency);

	List<Account> findAllByUserId(Long userId);

	Optional<Account> findByUserIdAndCurrency(Long userId, Currency currency);

	Optional<Account> findById(Long id);

	boolean existsByUserId(Long userId);

	Page<Account> findAllByUserId(Long userId, Pageable pageable);

}
