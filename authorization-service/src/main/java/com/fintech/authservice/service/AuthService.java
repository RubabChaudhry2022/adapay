package com.fintech.authservice.service;
import com.fintech.authservice.dto.LoginRequest;
import com.fintech.authservice.dto.SignupRequest;
import com.fintech.authservice.dto.UserCreationRequest;
import com.fintech.authservice.dto.UserResponse;
import com.fintech.authservice.model.Role;
import com.fintech.authservice.model.User;
import com.fintech.authservice.repository.UserRepository;

import jakarta.validation.Valid;

import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
@Slf4j
@Service
public class AuthService {
@Autowired
private UserRepository userRepository;
@Autowired 
private PasswordEncoder passwordEncoder;
@Autowired
private JwtService jwtService;

public ResponseEntity<?> signup(SignupRequest request) {

    if (userRepository.findByEmail(request.getEmail()).isPresent()) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Email already exists"));
    }

    User user = new User();
    user.setFirstName(request.getFirstName());
    user.setLastName(request.getLastName());
    user.setEmail(request.getEmail());
    user.setPhone_number(request.getPhone_number());
    user.setPassword(passwordEncoder.encode(request.getPassword()));

    // Assign role
    user.setRole(user.getEmail().endsWith("@vaultspay.com") ? Role.ADMIN : Role.USER);

    userRepository.save(user);

  
    String fullName = user.getFirstName() + " " + user.getLastName();

    return ResponseEntity.ok(Map.of(
        "message", "Account created successfully",
        "user", new UserResponse(fullName, user.getEmail(), user.getPhone_number())
    ));
}


public ResponseEntity<?> login(LoginRequest request) {
	Optional<User> userOpt = userRepository.findByEmail(request.getEmail());
	if(userOpt.isEmpty()) {
		 return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Email does not exist"));
	}
	User user=userOpt.get();
	 if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
         return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Incorrect password"));
     }
	

	 String token = jwtService.generateToken(user.getEmail(),user.getId(), user.getRole());
	 String refreshToken = jwtService.generateRefreshToken(user.getEmail(),user.getId(), user.getRole());
	 return ResponseEntity.ok(Map.of(
             "message", "Login successful",
             "accessToken", token ,
             "refreshToken", refreshToken      
     ));
}
//checks role
private Optional<User> getUserIfAdmin(String token) {
    if (!jwtService.isAccessToken(token)) {
    	 log.warn("Invalid token type received.");
    	 return Optional.empty();
    }

    String email = jwtService.extractEmail(token);
    Optional<User> userOpt = userRepository.findByEmail(email);

    if (userOpt.isEmpty()) {
    	log.warn("No user found with email extracted from token: {}", email);
    	return Optional.empty();
    }

    User user = userOpt.get();
    if (user.getRole() != Role.ADMIN) return Optional.empty();

    return Optional.of(user); // valid admin
}

public ResponseEntity<?> users(String token){
	
	  log.debug("Received token for /admin/viewUsers = {}", token);
	 Optional<User> admin = getUserIfAdmin(token);
	
	 if (admin.isEmpty()) {
		  log.warn("Unauthorized access attempt to view users.");
	        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Access denied"));
	    }
	  log.info("Admin {} is viewing all users", admin.get().getEmail());
      return ResponseEntity.ok(userRepository.findAll());
      }



public ResponseEntity<?> createUsers(String token, @Valid UserCreationRequest request) {
    if (!jwtService.isAccessToken(token)) {
        log.warn("Rejected request: Invalid access token");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Invalid token"));
    }

    Optional<User> admin = getUserIfAdmin(token);
    if (admin.isEmpty()) {
        log.warn("Rejected request: Non-admin tried to create a user");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Access denied"));
    }

    if (userRepository.findByEmail(request.getEmail()).isPresent()) {
        log.warn("Rejected request: Email already exists - {}", request.getEmail());
        return ResponseEntity.badRequest().body(Map.of("error", "Email already exists"));
    }

    // ADMIN role must have @vaultspay.com email
    if (request.getRole() == Role.ADMIN && !request.getEmail().endsWith("@vaultspay.com")) {
        log.warn("Rejected request: Admin email must end with @vaultspay.com - {}", request.getEmail());
        return ResponseEntity.badRequest().body(Map.of("error", "Admin email must end with @vaultspay.com"));
    }

    User user = new User();
    user.setFirstName(request.getFirstName());
    user.setLastName(request.getLastName());
    user.setEmail(request.getEmail());
    user.setPhone_number(request.getPhone_number());
    user.setPassword(passwordEncoder.encode(request.getPassword()));
    user.setRole(request.getRole());

    userRepository.save(user);

    String fullName = user.getFirstName() + " " + user.getLastName();

    log.info("Admin {} created a new {} with email: {}", 
        admin.get().getEmail(), 
        user.getRole().name(), 
        user.getEmail()
    );

    return ResponseEntity.ok(Map.of(
        "message", user.getRole() + " created successfully",
        "user", new UserResponse(fullName, user.getEmail(), user.getPhone_number())
    ));
}
public ResponseEntity<?> refreshAccessToken(String refreshToken) {
    if (!jwtService.isRefreshToken(refreshToken)) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Invalid refresh token"));
    }

    String email = jwtService.extractEmail(refreshToken);
    User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

    String newAccessToken = jwtService.generateToken(user.getEmail(),user.getId(), user.getRole());

    return ResponseEntity.ok(Map.of("accessToken", newAccessToken));
}

}
