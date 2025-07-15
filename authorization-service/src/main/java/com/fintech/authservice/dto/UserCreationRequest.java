package com.fintech.authservice.dto;

import com.fintech.authservice.model.Role;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserCreationRequest {


	    @NotBlank
	    private String firstName;

	    @NotBlank
	    private String lastName;

	    @Email
	    private String email;

	    @NotBlank
	    private String phoneNumber;

	    @NotBlank
	    private String password;

	    private Role role; // USER or ADMIN

	 
	}
