package com.example.account.exception;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import java.time.ZonedDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({ResourceNotFoundException.class, ResourceAlreadyExistsException.class})
    public ResponseEntity<Object> handleCustomException(RuntimeException ex, HttpServletRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        HttpStatus status = ex instanceof ResourceNotFoundException ? HttpStatus.NOT_FOUND : HttpStatus.BAD_REQUEST;

        body.put("timestamp", ZonedDateTime.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", ex.getMessage());
        body.put("path", request.getRequestURI());

        return new ResponseEntity<>(body, status);
    }
}
