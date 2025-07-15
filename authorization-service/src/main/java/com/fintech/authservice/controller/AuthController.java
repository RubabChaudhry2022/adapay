package com.fintech.authservice.controller;
import com.fintech.authservice.dto.LoginRequest;
import com.fintech.authservice.dto.SignupRequest;
import com.fintech.authservice.dto.UserCreationRequest;
import com.fintech.authservice.service.AuthService;
import jakarta.validation.Valid;
<<<<<<< HEAD
import java.util.Map;
=======
>>>>>>> 022c01f (Removed apache-maven files and updated .gitignore)
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("v1/auth")
public class AuthController {

	@Autowired
<<<<<<< HEAD
	public AuthService authService;
@PostMapping("/signup")
public ResponseEntity<?> signup(@Valid @RequestBody SignupRequest request, BindingResult result){
    if (result.hasErrors()) {
        // Return first error message as response
        return ResponseEntity.badRequest().body(Map.of("error", result.getFieldError().getDefaultMessage()) );
    }
return authService.signup(request);
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

@GetMapping("/users")
public ResponseEntity<?> users(@RequestHeader("Authorization") String authHeader){
	 String token = authHeader.substring(7);
	  return authService.users(token);
}

@PostMapping("/addUsers")
public ResponseEntity<?> createUsers(@RequestHeader("Authorization") String authHeader,
	    @Valid @RequestBody UserCreationRequest request,
	    BindingResult result){ 
	if (result.hasErrors()) {
	        return ResponseEntity.badRequest().body(Map.of("error", result.getFieldError().getDefaultMessage())
		        );}
	 String token = authHeader.substring(7);
	  return authService.createUsers(token,request);
} 

@PostMapping("/refresh")
public ResponseEntity<?> refreshToken(@RequestHeader("Authorization") String authHeader) {
	 String refreshToken = authHeader.substring(7);
	  System.out.println("Received Refresh Token: " + refreshToken);
    return authService.refreshAccessToken(refreshToken);
  
}
=======
	private AuthService authService;

	@PostMapping("/signup")
	public ResponseEntity<?> signup(@Valid @RequestBody SignupRequest request) {
		return authService.signup(request);
	}

	@PostMapping("/login")
	public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
		return authService.login(request);
	}

	@PreAuthorize("hasAuthority('ADMIN')")
	@GetMapping("/users")
	public ResponseEntity<?> users() {
		return authService.viewUsers();
	}

	@PreAuthorize("hasAuthority('ADMIN')")
	@PostMapping("/users")
	public ResponseEntity<?> createUsers(@Valid @RequestBody UserCreationRequest request,
			Authentication authentication) {

		String email = authentication.getName();
		return authService.createUsers(email, request);
	}

	@PostMapping("/refresh")
	public ResponseEntity<?> refreshToken(@RequestHeader("Authorization") String authHeader) {
		String refreshToken = authHeader.substring(7);
		System.out.println("Received Refresh Token: " + refreshToken);
		return authService.refreshAccessToken(refreshToken);

	}
>>>>>>> 022c01f (Removed apache-maven files and updated .gitignore)

}