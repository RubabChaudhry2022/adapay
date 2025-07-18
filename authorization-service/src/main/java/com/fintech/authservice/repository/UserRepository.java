package com.fintech.authservice.repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.fintech.authservice.model.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}







//this basically works like it parses the method findbyemail and maps into sql like "SELECT * FROM users WHERE email=?"