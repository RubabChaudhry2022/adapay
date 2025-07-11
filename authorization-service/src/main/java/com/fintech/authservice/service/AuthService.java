package com.fintech.authservice.service;
import com.fintech.authservice.dto.LoginRequest;
import com.fintech.authservice.dto.UserResponse;
import com.fintech.authservice.model.Role;
import com.fintech.authservice.model.User;
import com.fintech.authservice.repository.UserRepository;

import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class AuthService {
@Autowired
private UserRepository userRepository;
@Autowired 
private PasswordEncoder passwordEncoder;
@Autowired
private JwtService jwtService;

public ResponseEntity<?> signup(User user) {

    if (userRepository.findByEmail(user.getEmail()).isPresent()) {
        return ResponseEntity.badRequest().body(Map.of("error", "Email already exists"));
    }

    user.setPassword(passwordEncoder.encode(user.getPassword()));

    // Set role based on email
    user.setRole(user.getEmail().equalsIgnoreCase("admin@vaultspay.com") ? Role.ADMIN : Role.USER);

    userRepository.save(user);

    String token = jwtService.generateToken(user.getEmail(), user.getRole());
    String fullName = user.getFirstname() + " " + user.getLastname();

    return ResponseEntity.ok(Map.of(
        "message", "Account created successfully",
        "user", new UserResponse(fullName, user.getEmail(), user.getNumber())
    ));
}

public ResponseEntity<?> login(LoginRequest request) {
	Optional<User> userOpt = userRepository.findByEmail(request.getEmail());
	if(userOpt.isEmpty()) {
		 return ResponseEntity.badRequest().body(Map.of("error", "Email does not exist"));
	}
	User user=userOpt.get();
	 if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
         return ResponseEntity.badRequest().body(Map.of("error", "Incorrect password"));
     }
	

	 String token = jwtService.generateToken(user.getEmail(), user.getRole());
	 return ResponseEntity.ok(Map.of(
             "message", "Login successful",
             "token", token        
     ));
}
public ResponseEntity<?> viewUsers(String token){
	 if (!jwtService.validateToken(token)) {
         return ResponseEntity.badRequest().body(Map.of("error", "Invalid token"));
     }
	String role=jwtService.extractRole(token);
	 if (!"ADMIN".equalsIgnoreCase(role)) {
	        return ResponseEntity.badRequest().body(Map.of("error", "Access denied"));
	    }
	 System.out.println("Received Token = " + token);
	 System.out.println("Extracted Role = " + jwtService.extractRole(token));

	 return ResponseEntity.ok(userRepository.findAll());
}



public ResponseEntity<?> addUser(String token, User newUser){
	 if (!jwtService.validateToken(token)) {
         return ResponseEntity.badRequest().body(Map.of("error", "Invalid token"));
     }
	String role=jwtService.extractRole(token);
	System.out.println("Role from token: " + role);
	 if (!"ADMIN".equalsIgnoreCase(role)) {
	        return ResponseEntity.badRequest().body(Map.of("error", "Access denied"));
	    }
	 if(userRepository.findByEmail(newUser.getEmail()).isPresent()) {
		 return ResponseEntity.badRequest().body(Map.of("error", "Email already exists"));
	 }
	 newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
	    newUser.setRole(Role.USER); // All added users are USER

	    userRepository.save(newUser);

	    String newToken = jwtService.generateToken(newUser.getEmail(), newUser.getRole());
	    String fullName = newUser.getFirstname() + " " + newUser.getLastname();

	    return ResponseEntity.ok(Map.of(
	        "message", "User added successfully",
	        "token", newToken,
	        "user", new UserResponse(fullName, newUser.getEmail(), newUser.getNumber())
	    ));

}

//Delete a user

public ResponseEntity<?> deleteUser(String token,String delEmail){
	 if (!jwtService.validateToken(token)) {
         return ResponseEntity.badRequest().body(Map.of("error", "Invalid token"));
     }
	String role=jwtService.extractRole(token);
	 if (!"ADMIN".equalsIgnoreCase(role)) {
	        return ResponseEntity.badRequest().body(Map.of("error", "Access denied"));
	    }
	 Optional<User> userOpt= userRepository.findByEmail(delEmail);
	 if(userOpt.isEmpty()) {
		 return ResponseEntity.badRequest().body(Map.of("error", "Email does not exist"));
	 }
	 userRepository.delete(userOpt.get());
	 return ResponseEntity.ok(Map.of("message","user deleted successfully"));
}

//add admin
public ResponseEntity<?> addAdmin(String token, User newAdmin){
	 if (!jwtService.validateToken(token)) {
        return ResponseEntity.badRequest().body(Map.of("error", "Invalid token"));
    }
	String role=jwtService.extractRole(token);
	 if (!"ADMIN".equalsIgnoreCase(role)) {
	        return ResponseEntity.badRequest().body(Map.of("error", "Access denied"));
	    }
	 if(userRepository.findByEmail(newAdmin.getEmail()).isPresent()) {
		 return ResponseEntity.badRequest().body(Map.of("error", "Email already exists"));
	 }
	 newAdmin.setPassword(passwordEncoder.encode(newAdmin.getPassword()));
    newAdmin.setRole(Role.ADMIN);
userRepository.save(newAdmin);

String newToken = jwtService.generateToken(newAdmin.getEmail(), newAdmin.getRole());
String fullName = newAdmin.getFirstname() + " " + newAdmin.getLastname();

return ResponseEntity.ok(Map.of(
    "message", "Admin added successfully",
    "token", newToken,
    "user", new UserResponse(fullName, newAdmin.getEmail(), newAdmin.getNumber())
));

//return ResponseEntity.ok(Map.of("message", "admin added successfully"));
}

}
