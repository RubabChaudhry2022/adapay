package com.example.account.model;
import com.example.account.model.enums.AccountStatus;
import com.example.account.model.enums.Currency;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;
@Entity
@Data
@Table(name = "accounts", uniqueConstraints = {
    @UniqueConstraint(columnNames = "userId"),
    @UniqueConstraint(columnNames = "accountNumber")  
})
public class AccountModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private Long userId;
    
    @NotNull
    private String title;
    
    @NotNull
    private String accountNumber;  

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal balance;

    @Enumerated(EnumType.STRING)
    private Currency currency = Currency.PKR;

    @Enumerated(EnumType.STRING)
    private AccountStatus status = AccountStatus.ACTIVE;

    private LocalDateTime createdAt = LocalDateTime.now();
    }
