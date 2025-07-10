package com.example.Account.AccountModel;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "accounts", uniqueConstraints = {
    @UniqueConstraint(columnNames = "userId")  //  Enforce unique userId 
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "User ID cannot be null")  //  Prevent null userId
    private Long userId;

    @NotNull(message = "Balance cannot be null")
    @DecimalMin(value = "0.0", inclusive = false, message = "Balance must be greater than 0")  //  Positive balance
    private BigDecimal balance;
    
    @NotNull(message = "Name cannot be null")
    private String name;
    
    
    private String currency="PKR";

    private String status= "ACTIVE"; 

    private LocalDateTime createdAt = LocalDateTime.now();
}


