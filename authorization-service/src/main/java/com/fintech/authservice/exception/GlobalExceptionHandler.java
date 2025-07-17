package com.fintech.authservice.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(ResourceAlreadyExistsException.class)
	public ResponseEntity<ErrorResponse> handleAlreadyExists(ResourceAlreadyExistsException ex,
			HttpServletRequest request) {
		return buildErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST, request);
	}

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
		return buildErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND, request);
	}

	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex, HttpServletRequest request) {
		return buildErrorResponse(ex.getMessage(), HttpStatus.FORBIDDEN, request);
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex,
			HttpServletRequest request) {
		return buildErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST, request);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex,
			HttpServletRequest request) {
		String message = ex.getBindingResult().getFieldError().getDefaultMessage();
		return buildErrorResponse(message, HttpStatus.BAD_REQUEST, request);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleGeneric(Exception ex, HttpServletRequest request) {
		return buildErrorResponse("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR, request);
	}

	private ResponseEntity<ErrorResponse> buildErrorResponse(String message, HttpStatus status,
			HttpServletRequest request) {
		ErrorResponse error = new ErrorResponse(LocalDateTime.now(), status.value(), status.getReasonPhrase(), message,
				request.getRequestURI());
		return ResponseEntity.status(status).body(error);
	}
}
