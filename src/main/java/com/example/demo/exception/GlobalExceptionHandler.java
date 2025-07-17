package com.example.demo.exception;

import java.time.ZonedDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(Exception.class)
	public ResponseEntity<Object> handleGeneralException(Exception ex, HttpServletRequest request) {
		Map<String, Object> body = new LinkedHashMap<>();
		body.put("timestamp", ZonedDateTime.now());
		body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
		body.put("error", HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
		body.put("message", ex.getMessage());
		body.put("path", request.getRequestURI());

		ex.printStackTrace();
		return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
	}

}