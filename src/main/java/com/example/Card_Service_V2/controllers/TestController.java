package com.example.Card_Service_V2.controllers;

import com.example.Card_Service_V2.services.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class TestController {

    @Autowired
    private JwtUtil jwtUtil;

    // Generate ADMIN token for testing
    @GetMapping("/admin-token")
    public ResponseEntity<?> generateAdminToken() {
        String token = jwtUtil.generateToken("1", "ADMIN");

        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        response.put("userId", "1");
        response.put("role", "ADMIN");

        return ResponseEntity.ok(response);
    }

    // Generate USER token for testing
    @GetMapping("/user-token")
    public ResponseEntity<?> generateUserToken() {
        String token = jwtUtil.generateToken("2", "USER");

        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        response.put("userId", "2");
        response.put("role", "USER");

        return ResponseEntity.ok(response);
    }

    // Generate custom token
    @PostMapping("/generate-token")
    public ResponseEntity<?> generateCustomToken(@RequestBody Map<String, String> request) {
        String userId = request.get("userId");
        String role = request.get("role");

        if (userId == null || role == null) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "userId and role are required");
            return ResponseEntity.badRequest().body(errorResponse);
        }

        String token = jwtUtil.generateToken(userId, role);

        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        response.put("userId", userId);
        response.put("role", role);

        return ResponseEntity.ok(response);
    }

    // Validate token endpoint (optional - for testing)
    @PostMapping("/validate-token")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.badRequest().body("Invalid Authorization header format");
            }

            String token = authHeader.substring(7);
            // You might need to create a validateToken method in JwtUtil if it doesn't exist
            // For now, this is a placeholder
            
            Map<String, String> response = new HashMap<>();
            response.put("status", "Token validation endpoint");
            response.put("message", "Token format is valid");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Token validation failed");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(401).body(errorResponse);
        }
    }
}