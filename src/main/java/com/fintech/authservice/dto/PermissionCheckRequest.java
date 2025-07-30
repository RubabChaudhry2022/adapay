package com.fintech.authservice.dto;

import lombok.Data;

@Data
public class PermissionCheckRequest {
	private String httpMethod;
	private String url;
}