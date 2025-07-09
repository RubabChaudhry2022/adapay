package com.example.Project.auth.service;

import com.example.Project.auth.client.AuthServiceClient;
import com.example.Project.auth.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final AuthServiceClient authServiceClient;

    public Optional<UserDto> getUserById(Long userId) {
        return authServiceClient.getUserById(userId);
    }

    public boolean isUserActive(Long userId) {
        return getUserById(userId)
                .map(UserDto::isActive)
                .orElse(false);
    }
}
