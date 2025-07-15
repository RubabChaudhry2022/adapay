package com.fintech.authservice.dto;

import com.fintech.authservice.model.Role;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserCreationRequest {

<<<<<<< HEAD
	    @NotBlank
	    private String firstName;

	    @NotBlank
	    private String lastName;

	    @Email
	    private String email;

	    @NotBlank
	    private String phone_number;

	    @NotBlank
	    private String password;

	    private Role role; // USER or ADMIN

	 
	}
=======
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
>>>>>>> 022c01f (Removed apache-maven files and updated .gitignore)
