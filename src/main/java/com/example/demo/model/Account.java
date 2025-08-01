package com.example.demo.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.example.demo.enums.AccountStatus;
import com.example.demo.enums.Currency;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Data
@Table(name = "accounts", uniqueConstraints = { 
		@UniqueConstraint(columnNames = "accountNumber"),
		@UniqueConstraint(columnNames = "userId")

})
public class Account {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	@Column(name = "user_id", nullable = false)
	private Long userId;

	@NotNull
	private String title;

	@NotNull
	@Column(name = "account_number", unique = true, nullable = false)
	private String accountNumber;

	@NotNull
	//@DecimalMin(value = "0.0", inclusive = true)
	private BigDecimal balance;

	@Enumerated(EnumType.STRING)
	private Currency currency = Currency.PKR;

	@Enumerated(EnumType.STRING)
	private AccountStatus status = AccountStatus.ACTIVE;

	private LocalDateTime createdAt = LocalDateTime.now();

	@JsonIgnore
	@Column(name = "is_global_account", nullable = false)
	private boolean isGlobalAccount = false;
	
	  @Version
	    @Column(name = "version")
	    private Long version;
}