package com.example.Card_Service_V2.controllers;

import com.example.Card_Service_V2.models.CardModel;
import com.example.Card_Service_V2.services.CardService;
import com.example.Card_Service_V2.services.AuthService;
import com.example.Card_Service_V2.services.dtos.CardListDTO;
import com.example.Card_Service_V2.services.dtos.CreateCardDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/cards")
public class CardController {

    @Autowired
    private CardService cardService;

    @Autowired
    private AuthService authService;

    @GetMapping
    public ResponseEntity<?> cards(@RequestHeader("Authorization") String authHeader) {
        Map<String, Object> userInfo = authService.validateToken(authHeader);
        if (userInfo == null) {
            return ResponseEntity.status(401).body("Invalid or expired token");
        }

        String role = (String) userInfo.get("role");

        if (!"ADMIN".equalsIgnoreCase(role)) {
            return ResponseEntity.status(403).body("Access denied. Only ADMIN can view all cards");
        }

        List<CardListDTO> cards = cardService.getCards();
        return ResponseEntity.ok(cards);
    }

    @GetMapping("/{accountId}")
    public ResponseEntity<?> getAllAccountCards(@PathVariable int accountId, @RequestHeader("Authorization") String authHeader) {
        // Validate token and get user info
        Map<String, Object> userInfo = authService.validateToken(authHeader);
        if (userInfo == null) {
            return ResponseEntity.status(401).body("Invalid or expired token");
        }

        String role = (String) userInfo.get("role");
        String userId = (String) userInfo.get("userId");

        // ADMIN can see any account's cards, USER can only see their own
        if ("ADMIN".equalsIgnoreCase(role)) {
            List<CardModel> cards = cardService.getAllCardsByAccountId(accountId);
            return ResponseEntity.ok(cards);
        } else if ("USER".equalsIgnoreCase(role)) {
            // Check if user owns this account (you might need to implement this logic)
            if (cardService.isAccountOwnedByUser(accountId, Integer.parseInt(userId))) {
                List<CardModel> cards = cardService.getAllCardsByAccountId(accountId);
                return ResponseEntity.ok(cards);
            } else {
                return ResponseEntity.status(403).body("Access denied. You can only view your own cards");
            }
        }

        return ResponseEntity.status(403).body("Access denied");
    }

    @PostMapping("/{userId}/account/{accountId}")
    public ResponseEntity<?> createCard(@PathVariable int userId, @PathVariable int accountId,
                                        @RequestBody CardModel card, @RequestHeader("Authorization") String authHeader) {
        Map<String, Object> userInfo = authService.validateToken(authHeader);
        if (userInfo == null) {
            return ResponseEntity.status(401).body("Invalid or expired token");
        }

        String role = (String) userInfo.get("role");
        String tokenUserId = (String) userInfo.get("userId");

        if ("ADMIN".equalsIgnoreCase(role) ||
                ("USER".equalsIgnoreCase(role) && Integer.parseInt(tokenUserId) == userId)) {

            CreateCardDTO createdCard = cardService.createCard(userId, accountId, card);
            return ResponseEntity.ok(createdCard);
        }

        return ResponseEntity.status(403).body("Access denied. You can only create cards for yourself");
    }

    @GetMapping("/search")
    public ResponseEntity<?> cards(CardModel card, @RequestHeader("Authorization") String authHeader) {
        // Validate token and get user info
        Map<String, Object> userInfo = authService.validateToken(authHeader);
        if (userInfo == null) {
            return ResponseEntity.status(401).body("Invalid or expired token");
        }

        // Both ADMIN and USER can search (but results might be filtered by service layer)
        List<CardModel> cards = cardService.getcards(card);
        return ResponseEntity.ok(cards);
    }

    @PutMapping("/{cardId}/block")
    public ResponseEntity<?> blockCard(@PathVariable int cardId, @RequestHeader("Authorization") String authHeader) {
        Map<String, Object> userInfo = authService.validateToken(authHeader);
        if (userInfo == null) {
            return ResponseEntity.status(401).body("Invalid or expired token");
        }

        String role = (String) userInfo.get("role");

        if (!"ADMIN".equalsIgnoreCase(role)) {
            return ResponseEntity.status(403).body("Access denied. Only ADMIN can block cards");
        }

        CardModel blockedCard = cardService.blockcard(cardId);
        return ResponseEntity.ok("Card blocked: " + blockedCard.getCardnumber());
    }
     @PutMapping("/{cardId}/unblock")
    public ResponseEntity<?> unblockCard(@PathVariable int cardId, @RequestHeader("Authorization") String authHeader) {
        Map<String, Object> userInfo = authService.validateToken(authHeader);
        if (userInfo == null) {
            return ResponseEntity.status(401).body("Invalid or expired token");
        }

        String role = (String) userInfo.get("role");

        if (!"ADMIN".equalsIgnoreCase(role)) {
            return ResponseEntity.status(403).body("Access denied. Only ADMIN can block cards");
        }

        CardModel unblockedCard = cardService.unblockCard(cardId);
        return ResponseEntity.ok("Card unblocked: " + unblockedCard.getCardnumber());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> findByUser(@PathVariable int userId, @RequestHeader("Authorization") String authHeader) {
        try {
            Map<String, Object> userInfo = authService.validateToken(authHeader);
            if (userInfo == null) {
                return ResponseEntity.status(401).body("Invalid or expired token");
            }

            String role = (String) userInfo.get("role");
            Object tokenUserIdObj = userInfo.get("userId");

            int tokenUserId;
            if (tokenUserIdObj instanceof String) {
                tokenUserId = Integer.parseInt((String) tokenUserIdObj);
            } else if (tokenUserIdObj instanceof Integer) {
                tokenUserId = (Integer) tokenUserIdObj;
            } else {
                return ResponseEntity.status(400).body("Invalid token format");
            }

            if ("USER".equalsIgnoreCase(role) && tokenUserId == userId) {
                List<CardModel> userCards = cardService.findByUser(userId);
                return ResponseEntity.ok(userCards != null ? userCards : new ArrayList<>());
            }

            return ResponseEntity.status(403).body("Access denied. You can only view your own cards");

        } catch (NumberFormatException e) {
            return ResponseEntity.status(400).body("Invalid user ID format in token");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Internal server error");
        }
    }
        

    @PutMapping("/{cardId}")
    public ResponseEntity<?> updateCard(@PathVariable int cardId, @RequestBody Map<String, String> body,
                                        @RequestHeader("Authorization") String authHeader) {
        Map<String, Object> userInfo = authService.validateToken(authHeader);
        System.out.println(cardId + " " + body + " " + authHeader);
        if (userInfo == null) {
            return ResponseEntity.status(401).body("Invalid or expired token");
        }

        String role = (String) userInfo.get("role");
        String userId = (String) userInfo.get("userId");
        System.out.println(userId + " " + role);

        if ("USER".equalsIgnoreCase(role)) {
            if (!cardService.isCardOwnedByUser(cardId, Integer.parseInt(userId))) {
                return ResponseEntity.status(403).body("Access denied. You can only update your own cards");
            }
        }



        if (body.containsKey("cardpin")) {
            String pin = body.get("cardpin");
            if (!pin.matches("\\d{3}")) {
                return ResponseEntity.badRequest().body("Invalid PIN. It must be exactly 3 digits.");
            }
            cardService.changepin(cardId, pin);
            System.out.println("cardId = " + cardId + ", body = " + body + ", authHeader = " + authHeader + pin);
        }

        if (body.containsKey("cardstatus")) {
            String status = body.get("cardstatus").toLowerCase();
            if (!status.equals("freeze") && !status.equals("active")) {
                return ResponseEntity.badRequest().body("Invalid status. Allowed values: freeze, active.");
            }
            cardService.freezecard(cardId, status);
        }

        return ResponseEntity.ok("Card updated");
    }

    
}