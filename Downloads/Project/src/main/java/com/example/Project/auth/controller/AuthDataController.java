package com.example.Project.auth.controller;

import com.example.Project.auth.dto.CardDto;
import com.example.Project.auth.dto.UserDto;
import com.example.Project.auth.service.CardService;
import com.example.Project.auth.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth-data")
@RequiredArgsConstructor
public class AuthDataController {

    private final UserService userService;
    private final CardService cardService;

    @GetMapping("/users/{userId}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long userId) {
        return userService.getUserById(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/users/{userId}/cards")
    public ResponseEntity<List<CardDto>> getUserCards(@PathVariable Long userId) {
        List<CardDto> cards = cardService.getUserCards(userId);
        return ResponseEntity.ok(cards);
    }

    @GetMapping("/cards/{cardId}")
    public ResponseEntity<CardDto> getCardById(@PathVariable Long cardId) {
        return cardService.getCardById(cardId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/users/{userId}/validate-card/{cardId}")
    public ResponseEntity<Boolean> validateUserCard(
            @PathVariable Long userId,
            @PathVariable Long cardId) {
        boolean isValid = cardService.validateUserCard(userId, cardId);
        return ResponseEntity.ok(isValid);
    }
}
