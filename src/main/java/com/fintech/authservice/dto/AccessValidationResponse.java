package com.fintech.authservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AccessValidationResponse {
	private boolean isAllowed;
}
