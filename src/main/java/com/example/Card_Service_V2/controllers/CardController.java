package com.example.Card_Service_V2.controllers;

import com.example.Card_Service_V2.models.CardModel;
import com.example.Card_Service_V2.services.CardService;
import com.example.Card_Service_V2.services.dtos.CardListDTO;
import com.example.Card_Service_V2.services.dtos.CardVerificationResponseDTO;
import com.example.Card_Service_V2.services.dtos.CreateCardDTO;
import com.example.Card_Service_V2.services.dtos.CreateCardRequestDTO;
import com.example.Card_Service_V2.services.dtos.UpdateCardRequestDTO;
import com.example.Card_Service_V2.services.dtos.InternalCardVerificationRequestDTO;
import com.example.Card_Service_V2.services.dtos.InternalCardVerificationResponseDTO;
import com.example.Card_Service_V2.utils.AuthUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/cards")
public class CardController {

    @Autowired
    private CardService cardService;

    @GetMapping
    public ResponseEntity<?> cards(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Integer userId,
            @RequestParam(required = false) Integer accountId,
            @RequestParam(required = false) String network,
            HttpServletRequest request) {

        if (!AuthUtils.isAdmin(request) && !AuthUtils.isUser(request)) {
            return ResponseEntity.status(403).body("Access denied. Invalid role");
        }

        boolean isAdmin = AuthUtils.isAdmin(request);
        Integer tokenUserId = AuthUtils.getUserIdAsInt(request);

        List<CardListDTO> cards = cardService.processCardListing(
                status, type, userId, accountId, network, isAdmin, tokenUserId);

        return ResponseEntity.ok(cards);
    }

    @GetMapping("{userId}/verify/{cardNumber}")
    public ResponseEntity<?> verifyCard(@PathVariable int userId, @PathVariable String cardNumber,
            HttpServletRequest request) {

        if (!AuthUtils.canAccessUser(request, userId)) {
            return ResponseEntity.status(403).body("Access denied. You can only verify your own cards");
        }

        CardVerificationResponseDTO result = cardService.processCardVerification(userId, cardNumber);

        if (result.isVerified()) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(400).body(result);
        }
    }

    @PostMapping
    public ResponseEntity<?> createCard(@RequestBody CreateCardRequestDTO request, HttpServletRequest httpRequest) {
        
        Integer userId = AuthUtils.getUserIdAsInt(httpRequest);
        if (userId == null) {
            return ResponseEntity.status(401).body("User ID not found in token. Please ensure you're authenticated");
        }
        
        request.setUserId(userId);
        
        System.out.println("🎫 CardController.createCard: Using userId " + userId + " from JWT token");

        try {
            CreateCardDTO createdCard = cardService.processCardCreation(request);
            return ResponseEntity.ok(createdCard);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Internal server error during card creation");
        }
    }

    @PutMapping("/{cardId}/block")
    public ResponseEntity<?> blockCard(@PathVariable int cardId, HttpServletRequest request) {

        if (!AuthUtils.isAdmin(request)) {
            return ResponseEntity.status(403).body("Access denied. Only ADMIN can block cards");
        }

        CardModel blockedCard = cardService.blockcard(cardId);
        return ResponseEntity.ok("Card blocked: " + blockedCard.getCardnumber());
    }

    @PutMapping("/{cardId}/unblock")
    public ResponseEntity<?> unblockCard(@PathVariable int cardId, HttpServletRequest request) {

        if (!AuthUtils.isAdmin(request)) {
            return ResponseEntity.status(403).body("Access denied. Only ADMIN can unblock cards");
        }

        CardModel unblockedCard = cardService.unblockCard(cardId);
        return ResponseEntity.ok("Card unblocked: " + unblockedCard.getCardnumber());
    }

    @PutMapping("/{cardId}")
    public ResponseEntity<?> updateCard(@PathVariable int cardId, @RequestBody UpdateCardRequestDTO request,
            HttpServletRequest httpRequest) {

        if (AuthUtils.isUser(httpRequest)) {
            Integer userId = AuthUtils.getUserIdAsInt(httpRequest);
            if (userId == null || !cardService.canUserAccessCard(cardId, userId)) {
                return ResponseEntity.status(403).body("Access denied. You can only update your own cards");
            }
        }

        try {
            cardService.processCardUpdate(cardId, request);
            return ResponseEntity.ok("Card updated successfully");

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Internal server error during card update");
        }
    }

    @PostMapping("/internal/verify")
    
    public ResponseEntity<?> internalVerifyCard(@RequestBody InternalCardVerificationRequestDTO request) {
System.out.println("🔍 CardController.internalVerifyCard: Processing internal card verification for userId " + request.getUserId());
        boolean response = cardService.processInternalCardVerification(request);
        return ResponseEntity.ok(response);
    }

}