package com.example.demo.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.example.demo.enums.TransactionStatus;
import com.example.demo.enums.TransactionType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "global_ledger")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GlobalLedgerEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String transactionReferenceId;

    @Enumerated(EnumType.STRING)
    private TransactionType type;

    @Enumerated(EnumType.STRING)
    private TransactionStatus status;

    private BigDecimal amount;

    private String narration;

    private Long senderId;     
    private Long receiverId;   

    private LocalDateTime createdAt = LocalDateTime.now();
}
