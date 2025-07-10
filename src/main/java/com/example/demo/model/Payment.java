package com.example.demo.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long senderId;
    private Long receiverId;

    private BigDecimal amount;

    private String status;  // SUCCESS, FAILED
    private String type;    // DEPOSIT, WITHDRAW, TRANSFER, RECEIVE

    private LocalDateTime createdAt = LocalDateTime.now();

    private Long userId; // Used for DEPOSIT/WITHDRAW/created by/initiated by
    
}

