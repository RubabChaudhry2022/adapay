package com.fintech.authservice.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "permissions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Permission {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String roles; // "ADMIN", "USER"
	private String method; // "GET", "POST"
	private String path; // "/v1/auth/users", "/v1/auth/users/*"
}
