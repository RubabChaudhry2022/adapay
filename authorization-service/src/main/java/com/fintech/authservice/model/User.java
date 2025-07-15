package com.fintech.authservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

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


	@NotBlank(message = "Password is required")
	private String password;

	@Email(message = "Invalid email format")
	@NotBlank(message = "Email is required")
	@Column(unique = true)
	private String email;


    @NotBlank(message = "Phone number is required")
    @Pattern(
        regexp = "^\\d{11}$",  
        message = "Phone number must be 11 digits"
    )
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    private Role role;
    

}
