package com.example.Project.auth.client;

import com.example.Project.auth.dto.CardDto;
import com.example.Project.auth.dto.UserDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class AuthServiceClient {

    private final RestTemplate restTemplate;
    private final String authServiceUrl;

    public AuthServiceClient(
            @Qualifier("authServiceRestTemplate") RestTemplate restTemplate,
            @Value("${auth-service.url}") String authServiceUrl) {
        this.restTemplate = restTemplate;
        this.authServiceUrl = authServiceUrl;
    }

    public Optional<UserDto> getUserById(Long userId) {
        try {
            ResponseEntity<UserDto> response = restTemplate.getForEntity(
                    authServiceUrl + "/users/{userId}",
                    UserDto.class,
                    userId
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return Optional.of(response.getBody());
            }
            return Optional.empty();
        } catch (RestClientException e) {
            log.error("Error fetching user data from Auth service: {}", e.getMessage());
            return Optional.empty();
        }
    }

    public List<CardDto> getUserCards(Long userId) {
        try {
            ResponseEntity<CardDto[]> response = restTemplate.getForEntity(
                    authServiceUrl + "/users/{userId}/cards",
                    CardDto[].class,
                    userId
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return Arrays.asList(response.getBody());
            }
            return Collections.emptyList();
        } catch (RestClientException e) {
            log.error("Error fetching user cards from Auth service: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    public Optional<CardDto> getCardById(Long cardId) {
        try {
            ResponseEntity<CardDto> response = restTemplate.getForEntity(
                    authServiceUrl + "/cards/{cardId}",
                    CardDto.class,
                    cardId
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return Optional.of(response.getBody());
            }
            return Optional.empty();
        } catch (RestClientException e) {
            log.error("Error fetching card data from Auth service: {}", e.getMessage());
            return Optional.empty();
        }
    }
}
