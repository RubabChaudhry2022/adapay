package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TokenValidationResponse {
	private boolean valid;
	private String message;
	private String role;
	private Integer userId;
	private String username;
	private boolean hasPermission;
}
