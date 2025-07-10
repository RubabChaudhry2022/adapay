package com.fintech.authorization_service.repository;
import com.fintech.authorization_service.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}







//thisbasically works like it parses the method findbyemail and maps into sql like "SELECT * FROM users WHERE email=?"