package com.fintech.authservice.service;

import com.fintech.authservice.dto.LoginRequest;
import com.fintech.authservice.dto.SignupRequest;
import com.fintech.authservice.dto.UserCreationRequest;
import com.fintech.authservice.dto.UserResponse;
import com.fintech.authservice.exception.ResourceAlreadyExistsException;
import com.fintech.authservice.exception.ResourceNotFoundException;
import com.fintech.authservice.model.Role;
import com.fintech.authservice.model.User;
import com.fintech.authservice.repository.UserRepository;

<<<<<<< HEAD
import jakarta.validation.Valid;

import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
=======
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

>>>>>>> 022c01f (Removed apache-maven files and updated .gitignore)
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

<<<<<<< HEAD
import lombok.extern.slf4j.Slf4j;
=======
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
>>>>>>> 022c01f (Removed apache-maven files and updated .gitignore)
@Slf4j
@Service

public class AuthService {
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;

<<<<<<< HEAD
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
=======
	public ResponseEntity<?> signup(SignupRequest request) {
		if (userRepository.findByEmail(request.getEmail()).isPresent()) {
			throw new ResourceAlreadyExistsException("Email already exists");
		}

		User user = new User();
		user.setFirstName(request.getFirstName());
		user.setLastName(request.getLastName());
		user.setPhoneNumber(request.getPhoneNumber());
		user.setEmail(request.getEmail());
		user.setPassword(passwordEncoder.encode(request.getPassword()));

		// Assign role based on email domain
		Role assignedRole = request.getEmail().endsWith("@vaultspay.com") ? Role.ADMIN : Role.USER;
		user.setRole(assignedRole);

		userRepository.save(user);

		String fullName = user.getFirstName() + " " + user.getLastName();
		return ResponseEntity.ok(Map.of("message", "Account created successfully", "user",
				new UserResponse(fullName, user.getEmail(), user.getPhoneNumber())));
	}

	public ResponseEntity<?> login(LoginRequest request) {
		User user = userRepository.findByEmail(request.getEmail())
				.orElseThrow(() -> new ResourceNotFoundException("User not found"));

		if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
			throw new AccessDeniedException("Invalid credentials");
		}

		String accessToken = jwtService.generateToken(user.getEmail(), user.getId(), user.getRole());
		String refreshToken = jwtService.generateRefreshToken(user.getEmail(), user.getId(), user.getRole());
		Date expiration = jwtService.extractExpiration(accessToken);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return ResponseEntity.ok(Map.of("message", "Login successful", "accessToken", accessToken, "refreshToken",
				refreshToken, "expiresAt", sdf.format(expiration)));
	}
	/*
	 * public ResponseEntity<?> login(LoginRequest request) { try { User user =
	 * userRepository.findByEmail(request.getEmail()) .orElseThrow(() -> new
	 * ResourceNotFoundException("User not found"));
	 * 
	 * if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
	 * throw new AccessDeniedException("Invalid credentials"); }
	 * 
	 * String accessToken = jwtService.generateToken(user.getEmail(), user.getId(),
	 * user.getRole()); String refreshToken =
	 * jwtService.generateRefreshToken(user.getEmail(), user.getId(),
	 * user.getRole()); Date expiration = jwtService.extractExpiration(accessToken);
	 * 
	 * SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	 * 
	 * return ResponseEntity.ok(Map.of( "message", "Login successful",
	 * "accessToken", accessToken, "refreshToken", refreshToken, "expiresAt",
	 * sdf.format(expiration) ));
	 * 
	 * } catch (ResourceNotFoundException | AccessDeniedException e) { // known
	 * exceptions return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
	 * "error", e.getMessage() )); } catch (Exception e) { // unexpected exception
	 * e.printStackTrace(); // to log in console return
	 * ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of( "error",
	 * "An unexpected error occurred" )); } }
	 */

	private User getAuthenticatedUser() {
		String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		return userRepository.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found"));
	}

	public ResponseEntity<?> viewUsers() {
		User currentUser = getAuthenticatedUser();
		if (currentUser.getRole() != Role.ADMIN) {
			throw new AccessDeniedException("Access denied");

		}

		return ResponseEntity.ok(userRepository.findAll());
	}

	public ResponseEntity<?> createUsers(String adminEmail, UserCreationRequest request) {
		User currentUser = userRepository.findByEmail(adminEmail)
				.orElseThrow(() -> new ResourceNotFoundException("Admin user not found"));
		if (currentUser.getRole() != Role.ADMIN) {
			throw new AccessDeniedException("Access denied");

		}

		if (userRepository.findByEmail(request.getEmail()).isPresent()) {
			throw new ResourceAlreadyExistsException("Email already exists");
		}

		if (request.getRole() == Role.ADMIN && !request.getEmail().endsWith("@vaultspay.com")) {
			throw new IllegalArgumentException("Admin email must end with @vaultspay.com");
		}

		User user = new User();
		user.setFirstName(request.getFirstName());
		user.setLastName(request.getLastName());
		user.setPhoneNumber(request.getPhoneNumber());
		user.setEmail(request.getEmail());
		user.setPassword(passwordEncoder.encode(request.getPassword()));
		user.setRole(request.getRole());

		userRepository.save(user);

		return ResponseEntity.ok(Map.of("message", user.getRole() + " created successfully", "user", new UserResponse(
				user.getFirstName() + " " + user.getLastName(), user.getEmail(), user.getPhoneNumber())));
	}

	public ResponseEntity<?> refreshAccessToken(String refreshToken) {
		if (!jwtService.isRefreshToken(refreshToken)) {
			throw new AccessDeniedException("Invalid refresh token");
		}

		String email = jwtService.extractEmail(refreshToken);
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException("User not found"));
		String newAccessToken = jwtService.generateToken(user.getEmail(), user.getId(), user.getRole());

		return ResponseEntity.ok(Map.of("accessToken", newAccessToken));
	}
>>>>>>> 022c01f (Removed apache-maven files and updated .gitignore)

}
