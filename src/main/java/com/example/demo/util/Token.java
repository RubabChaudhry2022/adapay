package com.example.demo.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Date;

@Component
public class Token {

	private final String secret = "a123FZxys0987LmnopQR456TuvWXz123";

	public String generateAccountToken(Long accountId, String accountNumber, BigDecimal balance, String status) {
		return Jwts.builder().claim("accountId", accountId).claim("accountNumber", accountNumber)
				.claim("balance", balance).claim("status", status).claim("type", "ACCOUNT_ACCESS")
				.setIssuedAt(new Date()).setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 10))
				.signWith(SignatureAlgorithm.HS256, secret.getBytes()).compact();
	}

}
