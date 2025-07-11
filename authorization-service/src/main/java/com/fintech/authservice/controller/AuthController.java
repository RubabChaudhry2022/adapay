package com.fintech.authservice.controller;

import com.fintech.authservice.dto.LoginRequest;
import com.fintech.authservice.model.User;
import com.fintech.authservice.service.AuthService;

import jakarta.validation.Valid;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("v1/auth")
public class AuthController{
	
	@Autowired
	public AuthService authService;
@PostMapping("/signup")
public ResponseEntity<?> signup(@Valid @RequestBody User user, BindingResult result){
    if (result.hasErrors()) {
        // Return first error message as response
        return ResponseEntity.badRequest().body(Map.of("error", result.getFieldError().getDefaultMessage()) );
    }
return authService.signup(user);
}
@PostMapping("/login")
public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request, BindingResult result){
	 if (result.hasErrors()) {
	        return ResponseEntity.badRequest().body(
	            Map.of("error", result.getFieldError().getDefaultMessage())
	        );
	    }
return authService.login(request);
}
@GetMapping("/admin/Users")
public ResponseEntity<?> viewUsers(@RequestHeader("Authorization") String authHeader){
	 String token = authHeader.substring(7);
	  return authService.viewUsers(token);
}
@PostMapping("/admin/addUser")
public ResponseEntity<?> addUser(@RequestHeader("Authorization") String authHeader,
	    @Valid @RequestBody User newUser,
	    BindingResult result){
	 String token = authHeader.substring(7);
	  return authService.addUser(token,newUser);
} 
@DeleteMapping("/admin/delete")
public ResponseEntity<?> deleteUser(@RequestHeader("Authorization") String authHeader, @RequestParam String email) {
    String token = authHeader.substring(7);
    return authService.deleteUser(token, email);
}
@PostMapping("/admin/addAdmin")
public ResponseEntity<?> addAdmin(@RequestHeader("Authorization") String authHeader, @Valid @RequestBody User newAdmin, 
		BindingResult result){
	 String token = authHeader.substring(7);
	  return authService.addAdmin(token,newAdmin);
} 
}