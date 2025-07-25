package com.example.Card_Service_V2.controllers;

import com.example.Card_Service_V2.models.CardModel;
import com.example.Card_Service_V2.models.CardPlan;
import com.example.Card_Service_V2.services.CardService;
import com.example.Card_Service_V2.services.dtos.CardListDTO;
import com.example.Card_Service_V2.services.dtos.CardVerificationResponseDTO;
import com.example.Card_Service_V2.services.dtos.CreateCardDTO;
import com.example.Card_Service_V2.services.dtos.CreateCardRequestDTO;
import com.example.Card_Service_V2.services.dtos.UpdateCardRequestDTO;
import com.example.Card_Service_V2.services.dtos.InternalCardVerificationRequestDTO;
import com.example.Card_Service_V2.utils.AuthUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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
            @RequestParam(required = false) Integer accountId,//dto
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
    System.out.println("[CardController] Received createCard request: " + request);
    String token = httpRequest.getHeader("Authorization");
    if (token != null && token.startsWith("Bearer ")) {
        token = token.substring(7);
    }
    try {
        System.out.println("[CardController] Fetching account info from token: " + token);
        CardService.AccountInfo accountInfo = cardService.fetchAccountInfoFromToken(token, request.getCurrency());
        System.out.println("[CardController] AccountInfo: userId=" + accountInfo.userId + ", accountId=" + accountInfo.id);
        request.setUserId(accountInfo.userId);
        request.setAccountId(accountInfo.id);
        String accountCurrency = null;
        if (accountInfo instanceof CardService.AccountInfoWithCurrency) {
            accountCurrency = ((CardService.AccountInfoWithCurrency) accountInfo).currency;
        }
        if (request.getCurrency() == null && accountCurrency != null) {
            request.setCurrency(accountCurrency);
        }
        System.out.println("[CardController] Calling processCardCreationWithCurrency with request: " + request);
        CreateCardDTO createdCard = cardService.processCardCreationWithCurrency(request, token, accountCurrency);
        System.out.println("[CardController] Card created successfully: " + createdCard);
        return ResponseEntity.ok(createdCard);
    } catch (CardService.ValidationException e) {
        System.out.println("[CardController] ValidationException: " + e.getMessage());
        return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    } catch (IllegalArgumentException e) {
        System.out.println("[CardController] IllegalArgumentException: " + e.getMessage());
        return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    } catch (Exception e) {
        System.out.println("[CardController] Exception: " + e.getMessage());
        return ResponseEntity.status(500).body(Map.of("error", "Internal server error during card creation"));
    }
}
    @PutMapping("/{cardId}/block")
    public ResponseEntity<?> blockCard(@PathVariable int cardId, HttpServletRequest request) {

        if (!AuthUtils.isAdmin(request)) {
            return ResponseEntity.status(403).body(Map.of("error", "Access denied. Only ADMIN can block cards"));
        }

        CardModel blockedCard = cardService.blockCard(cardId);
        String cardNumber = (blockedCard.getSensitiveData() != null && blockedCard.getSensitiveData().getCardNumber() != null)
            ? blockedCard.getSensitiveData().getCardNumber() : "";
        return ResponseEntity.ok(Map.of(
            "message", "Card blocked"
        ));
    }

    @PutMapping("/{cardId}/unblock")
    public ResponseEntity<?> unblockCard(@PathVariable int cardId, HttpServletRequest request) {

        if (!AuthUtils.isAdmin(request)) {
            return ResponseEntity.status(403).body(Map.of("error", "Access denied. Only ADMIN can unblock cards"));
        }

        CardModel unblockedCard = cardService.unblockCard(cardId);
        String cardNumber = (unblockedCard.getSensitiveData() != null && unblockedCard.getSensitiveData().getCardNumber() != null)
            ? unblockedCard.getSensitiveData().getCardNumber() : "";
        return ResponseEntity.ok(Map.of(
            "message", "Card unblocked",
            "cardNumber", cardNumber
        ));
    }

    @PutMapping("/{cardId}")
    public ResponseEntity<?> updateCard(@PathVariable int cardId, @RequestBody UpdateCardRequestDTO request,
            HttpServletRequest httpRequest) {

        if (AuthUtils.isUser(httpRequest)) {
            Integer userId = AuthUtils.getUserIdAsInt(httpRequest);
            if (userId == null || !cardService.canUserAccessCard(cardId, userId)) {
                return ResponseEntity.status(403).body(Map.of("error", "Access denied. You can only update your own cards"));
            }
        }

        try {
            cardService.processCardUpdate(cardId, request);
            return ResponseEntity.ok(Map.of("message", "Card updated successfully"));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Internal server error during card update"));
        }
    }

    @PostMapping("/internal/verify")
    
    public ResponseEntity<?> internalVerifyCard(@RequestBody InternalCardVerificationRequestDTO request) {
System.out.println("🔍 CardController.internalVerifyCard: Processing internal card verification for accountId " + request.getAccountId());
        boolean response = cardService.processInternalCardVerification(request);
        return ResponseEntity.ok(response);
    }
    @PutMapping("/deliver/{cardId}")
    public ResponseEntity<?> deliverCard(@PathVariable int cardId, @RequestBody Map<String, String> body, HttpServletRequest request) {
        boolean isAdmin = AuthUtils.isAdmin(request);
        String newStatus = body.get("cardStatus");
        try {
            cardService.processDeliverCard(cardId, newStatus, isAdmin);
            return ResponseEntity.ok(Map.of("message", "Card status set to " + newStatus));
        } catch (CardService.ValidationException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/user/activate")
    public ResponseEntity<?> activateCardByUser(@RequestBody Map<String, String> body, HttpServletRequest request) {
        String cardNumber = body.get("cardNumber");
        String cardCvv = body.get("cardCvv");
        Integer userId = AuthUtils.getUserIdAsInt(request);
        try {
            cardService.processActivateCardByUser(cardNumber, cardCvv, userId);
            return ResponseEntity.ok(Map.of("message", "Card activated successfully"));
        } catch (CardService.ValidationException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{cardId}")
    public ResponseEntity<?> getUserCardSensitiveData(@PathVariable int cardId, HttpServletRequest request) {
        Integer userId = AuthUtils.getUserIdAsInt(request);
        if (userId == null) {
            return ResponseEntity.status(403).body(Map.of("error", "Unauthorized: user not found in token"));
        }
        try {
            Map<String, Object> result = cardService.getUserCardSensitiveData(cardId, userId);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(403).body(Map.of("error", e.getMessage()));
        }
    }

    

    @GetMapping("/number/{cardId}")
    public ResponseEntity<?> getCardNumber(@PathVariable int cardId, HttpServletRequest request) {
        Integer userId = AuthUtils.getUserIdAsInt(request);
        if (userId == null) {
            return ResponseEntity.status(403).body(Map.of("error", "Unauthorized: user not found in token"));
        }
        try {
            CardModel card = cardService.getCardById(cardId);
            if (card.getUserid() != userId) {
                return ResponseEntity.status(403).body(Map.of("error", "Access denied. Card does not belong to user"));
            }
            String cardNumber = card.getSensitiveData() != null ? card.getSensitiveData().getCardNumber() : null;
            return ResponseEntity.ok(Map.of("cardNumber", cardNumber));
        } catch (Exception e) {
            return ResponseEntity.status(404).body(Map.of("error", "Card not found"));
        }
    }

    @GetMapping("/cvv/{cardId}")
    public ResponseEntity<?> getCardCvv(@PathVariable int cardId, HttpServletRequest request) {
        Integer userId = AuthUtils.getUserIdAsInt(request);
        if (userId == null) {
            return ResponseEntity.status(403).body(Map.of("error", "Unauthorized: user not found in token"));
        }
        try {
            CardModel card = cardService.getCardById(cardId);
            if (card.getUserid() != userId) {
                return ResponseEntity.status(403).body(Map.of("error", "Access denied. Card does not belong to user"));
            }
            String cardCvv = card.getSensitiveData() != null ? card.getSensitiveData().getCardCvv() : null;
            return ResponseEntity.ok(Map.of("cardCvv", cardCvv));
        } catch (Exception e) {
            return ResponseEntity.status(404).body(Map.of("error", "Card not found"));
        }
    }
}

@RestControllerAdvice
class CardValidationExceptionHandler {
    @ExceptionHandler(CardService.ValidationException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(CardService.ValidationException ex) {
        return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
    }

}