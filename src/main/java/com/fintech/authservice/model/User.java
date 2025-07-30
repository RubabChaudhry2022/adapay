package com.fintech.authservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "users")

@ToString
@NoArgsConstructor
@AllArgsConstructor

@Data
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank(message = "First name is required")
	private String firstName;

	@NotBlank(message = "Last name is required")
	private String lastName;
	@JsonIgnore
	@NotBlank(message = "Password is required")
	private String password;

	@Email(message = "Invalid email format")
	@NotBlank(message = "Email is required")
	@Column(unique = true)
	private String email;

	@NotBlank(message = "Phone number is required")
	@Pattern(regexp = "^(03)[0-9]{9}$", message = "Phone number must start with 03 and be 11 digits long")
	private String phoneNumber;

	@Enumerated(EnumType.STRING)
	private Role role;

}
