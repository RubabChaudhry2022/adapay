package com.example.Project.auth.service;

import com.example.Project.auth.client.AuthServiceClient;
import com.example.Project.auth.dto.CardDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CardService {

    private final AuthServiceClient authServiceClient;

    public List<CardDto> getUserCards(Long userId) {
        return authServiceClient.getUserCards(userId);
    }

    public Optional<CardDto> getCardById(Long cardId) {
        return authServiceClient.getCardById(cardId);
    }

    public boolean validateUserCard(Long userId, Long cardId) {
        return getCardById(cardId)
                .map(card -> card.getUserId().equals(userId))
                .orElse(false);
    }
}
