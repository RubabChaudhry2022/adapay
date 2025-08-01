package com.example.demo.util;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;

public class HttpHeaderHelper {
	public static HttpEntity<Void> createAuthHeaders(String token) {
		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(token);
		return new HttpEntity<>(headers);
	}
}
