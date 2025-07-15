package com.fintech.authservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
<<<<<<< HEAD
    @Data
    public class LoginRequest {
    	@Email(message = "Invalid email format")
    	 @NotBlank(message = "Email is required")
        private String email;

    	    @NotBlank(message = "Password is required")
        private String password;
    	   
    }

 
=======

@Data
public class LoginRequest {
	@Email(message = "Invalid email format")
	@NotBlank(message = "Email is required")
	private String email;

	@NotBlank(message = "Password is required")
	private String password;
>>>>>>> 022c01f (Removed apache-maven files and updated .gitignore)

}
