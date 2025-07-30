package com.fintech.authservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LogoutRequest {
	private String accessToken;
	@NotBlank(message = "Refresh token is required")
	private String refreshToken;

}
