package com.fintech.authservice.service;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

@Service
public class TokenBlacklistService {

	private final Map<String, Instant> blacklistedTokens = new ConcurrentHashMap<>();

	public void blacklist(String token, Instant expiry) {
		blacklistedTokens.put(token, expiry);
	}

	public boolean isBlacklisted(String token) {
		Instant expiry = blacklistedTokens.get(token);
		if (expiry == null)
			return false;

		if (expiry.isBefore(Instant.now())) {
			blacklistedTokens.remove(token);
			return false;
		}

		return true;
	}
}
