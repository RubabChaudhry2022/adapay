package com.example.demo.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.example.demo.enums.AccountStatus;
import com.example.demo.enums.Currency;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Data
@Table(name = "accounts", uniqueConstraints = { @UniqueConstraint(columnNames = "userId"),
		@UniqueConstraint(columnNames = "accountNumber")

})
public class Account {
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
	@DecimalMin(value = "0.0", inclusive = true)
	private BigDecimal balance = BigDecimal.ZERO;

	@Enumerated(EnumType.STRING)
	private Currency currency = Currency.PKR;

	@Enumerated(EnumType.STRING)
	private AccountStatus status = AccountStatus.ACTIVE;

	private LocalDateTime createdAt = LocalDateTime.now();
}
