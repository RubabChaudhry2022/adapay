package com.fintech.authservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class SignupRequest {
	@NotBlank(message = "First name is required")
	private String firstName;

	@NotBlank(message = "Last name is required")
	private String lastName;

	@Pattern(regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z]+\\.(com|org)$", message = "Email must be valid and end with .com or .org")
	@NotBlank(message = "Email is required")
	private String email;

	@NotBlank(message = "Phone number is required")
	@Pattern(regexp = "^\\d{11}$", message = "Phone number must be 11 digits")
	private String phoneNumber;

	@NotBlank(message = "Password is required")
	private String password;

}

