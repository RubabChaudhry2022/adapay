package com.fintech.authservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import lombok.Data;
    


    @Data
    public class LoginRequest {
    	 @Pattern(
    	            regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z]+\\.(com|org)$",
    	            message = "Email must be valid and end with .com or .org"
    	        )
    	 @NotBlank(message = "Email is required")
        private String email;

    	    @NotBlank(message = "Password is required")
        private String password;
    }

 

