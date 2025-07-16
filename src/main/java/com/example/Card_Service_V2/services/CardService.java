package com.example.Card_Service_V2.services;

import com.example.Card_Service_V2.models.CardModel;
import com.example.Card_Service_V2.repositories.CardRepo;
import com.example.Card_Service_V2.services.dtos.CardFilterDTO;
import com.example.Card_Service_V2.services.dtos.CardListDTO;
import com.example.Card_Service_V2.services.dtos.CreateCardDTO;
import com.example.Card_Service_V2.services.dtos.CreateCardRequestDTO;
import com.example.Card_Service_V2.services.dtos.UpdateCardRequestDTO;
import com.example.Card_Service_V2.services.dtos.CardVerificationResponseDTO;
import com.example.Card_Service_V2.services.dtos.InternalCardVerificationRequestDTO;
import com.example.Card_Service_V2.services.dtos.InternalCardVerificationResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class CardService {

    @Autowired
    private CardRepo repo;

    private Random random = new Random();

    // ============================================================================
    // CARD CREATION METHODS
    // ============================================================================

    public CreateCardDTO processCardCreation(CreateCardRequestDTO request) {
        validateCreateCardRequest(request);

        CardModel card = new CardModel();
        card.setUserId(request.getUserId());
        card.setAccountid(request.getAccountId());
        card.setType(request.getType());
        card.setNetwork(request.getNetwork());
        card.setCardpin(request.getCardpin());

        return createCard(request.getUserId(), request.getAccountId(), card);
    }

    private CreateCardDTO createCard(int userId, int accid, CardModel card) {
        card.setCardnumber(generateCardNumber());
        card.setCardcvv(generateCVV());
        card.setCardexpiry(generateExpiry());

        if ("Physical".equalsIgnoreCase(card.getType())) {
            card.setCardstatus("PENDING");
        } else if ("Virtual".equalsIgnoreCase(card.getType())) {
            card.setCardstatus("ACTIVE");
        }

        card.setCreatedAt(LocalDateTime.now());
        card.setUserId(userId);
        card.setAccountid(accid);

        CardModel saved = repo.save(card);

        CreateCardDTO response = new CreateCardDTO();
        response.setCardId(saved.getId());
        response.setMaskedCardNumber(maskCardNumber(saved.getCardnumber()));
        response.setCardexpiry(saved.getCardexpiry());
        response.setCardstatus(saved.getCardstatus());
        response.setType(saved.getType());
        response.setCreatedAt(saved.getCreatedAt());

        return response;
    }

    private void validateCreateCardRequest(CreateCardRequestDTO request) {
        if (request.getUserId() == null) {
            throw new IllegalArgumentException("userId is missing - this should be set from the JWT token");
        }
        
        if (request.getAccountId() == null) {
            throw new IllegalArgumentException("accountId is required");
        }

        if (request.getCardpin() == null || request.getCardpin().trim().isEmpty()) {
            throw new IllegalArgumentException("cardpin is required");
        }

        if (!request.getCardpin().matches("\\d{4}")) {
            throw new IllegalArgumentException("Invalid PIN. It must be exactly 4 digits.");
        }

        if (request.getType() == null || request.getType().trim().isEmpty()) {
            throw new IllegalArgumentException("Card type is required");
        }

        if (request.getNetwork() == null || request.getNetwork().trim().isEmpty()) {
            throw new IllegalArgumentException("Network is required");
        }
    }

    // ============================================================================
    // CARD MANAGEMENT METHODS  
    // ============================================================================

    public CardModel blockcard(int id) {
        CardModel card = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Card not found with ID: " + id));
        card.setCardstatus("Blocked");
        return repo.save(card);
    }

    public CardModel unblockCard(int cardId) {
        CardModel card = repo.findById(cardId)
                .orElseThrow(() -> new RuntimeException("Card not found with ID: " + cardId));
        card.setCardstatus("Active");
        return repo.save(card);
    }

    public void processCardUpdate(int cardId, UpdateCardRequestDTO request) {
        validateUpdateCardRequest(request);

        boolean hasPin = request.getCardpin() != null && !request.getCardpin().trim().isEmpty();
        boolean hasStatus = request.getCardstatus() != null && !request.getCardstatus().trim().isEmpty();

        if (!hasPin && !hasStatus) {
            throw new IllegalArgumentException("At least one field (cardpin or cardstatus) must be provided for update");
        }

        if (hasPin) {
            String pin = request.getCardpin().trim();
            if (!pin.matches("\\d{4}")) {
                throw new IllegalArgumentException("Invalid PIN. It must be exactly 4 digits.");
            }
            changepin(cardId, pin);
        }

        if (hasStatus) {
            String status = request.getCardstatus().trim().toLowerCase();
            if (!status.equals("freeze") && !status.equals("active")) {
                throw new IllegalArgumentException("Invalid status. Allowed values: freeze, active.");
            }
            freezecard(cardId, status);
        }
    }

    private CardModel changepin(int id, String pin) {
        CardModel card = repo.findById(id).get();
        card.setCardpin(pin);
        return repo.save(card);
    }

    private void freezecard(int accountid, String status) {
        CardModel card = repo.findById(accountid).get();
        card.setCardstatus(status);
        repo.save(card);
    }

    private void validateUpdateCardRequest(UpdateCardRequestDTO request) {
        if (request == null) {
            throw new IllegalArgumentException("Update request cannot be null");
        }
    }

    // ============================================================================
    // CARD VERIFICATION METHODS
    // ============================================================================

    public CardVerificationResponseDTO processCardVerification(int userId, String cardNumber) {
        boolean isValid = verifyCard(userId, cardNumber);
        
        if (isValid) {
            String maskedNumber = cardNumber.substring(cardNumber.length() - 4);
            return new CardVerificationResponseDTO(
                true, 
                "Card verified successfully", 
                userId, 
                maskedNumber
            );
        } else {
            return new CardVerificationResponseDTO(
                false, 
                "Card verification failed. Card not found, inactive, or doesn't belong to user", 
                userId, 
                null
            );
        }
    }

    private boolean verifyCard(int userId, String cardNumber) {
        try {
            Optional<CardModel> cardOpt = repo.findByCardnumber(cardNumber);
            
            if (!cardOpt.isPresent()) {
                return false; 
            }
            
            CardModel card = cardOpt.get();
            
            if (card.getUserid() != userId) {
                return false; 
            }
            
            String status = card.getCardstatus();
            if (status == null || 
                "blocked".equalsIgnoreCase(status) || 
                "expired".equalsIgnoreCase(status) ||
                "suspended".equalsIgnoreCase(status)) {
                return false; 
            }
            
            return true; 
            
        } catch (Exception e) {
            return false;
        }
    }

    public boolean processInternalCardVerification(InternalCardVerificationRequestDTO request) {
        try {
            if (request.getCardNumber() == null || request.getCardNumber().trim().isEmpty()) {
            return false;
            }
            
            if (request.getCardPin() == null || request.getCardPin().trim().isEmpty()) {
                return false;
            }
            
            
            Optional<CardModel> cardOpt = repo.findByCardnumber(request.getCardNumber());
            if (!cardOpt.isPresent()) {
                return false;
            }
            
            CardModel card = cardOpt.get();
            
            if (!request.getCardPin().equals(card.getCardpin())) {
                return false;
            }
            
            if (request.getUserId() != null && !request.getUserId().equals(card.getUserid())) {
                return false;
            }
            
            String status = card.getCardstatus();
            if (status == null || "BLOCKED".equalsIgnoreCase(status) || 
                "EXPIRED".equalsIgnoreCase(status) || "SUSPENDED".equalsIgnoreCase(status)) {
                return false;
            }
            
            return true;
            
        } catch (Exception e) {
            return false;
        }
    }


    public List<CardListDTO> processCardListing(String status, String type, Integer userId, 
                                               Integer accountId, String network, 
                                               boolean isAdmin, Integer tokenUserId) {
        
        if (isAdmin) {
            if (status != null || type != null || userId != null || accountId != null || network != null) {
                CardFilterDTO filters = new CardFilterDTO();
                filters.setStatus(status);
                filters.setType(type);
                filters.setUserId(userId);
                filters.setAccountId(accountId);
                filters.setNetwork(network);
                
                return getFilteredCardsWithDTOForAdmin(filters);
            } else {
                return getCardsForAdmin();
            }
        } else {
            CardFilterDTO filters = new CardFilterDTO();
            filters.setStatus(status);
            filters.setType(type);
            filters.setUserId(tokenUserId);
            filters.setAccountId(accountId);
            filters.setNetwork(network);
            
            return getFilteredCardsWithDTOForUser(filters, tokenUserId);
        }
    }

    private List<CardListDTO> getCardsForAdmin() {
        List<CardModel> cards = repo.findAll();
        return convertToCardListDTOForAdmin(cards);
    }

    private List<CardListDTO> getFilteredCardsWithDTOForAdmin(CardFilterDTO filters) {
        List<CardModel> cards = repo.findWithFilters(
            filters.getStatus(),
            filters.getType(),
            filters.getUserId(),
            filters.getAccountId(),
            filters.getNetwork()
        );
        return convertToCardListDTOForAdmin(cards);
    }

    private List<CardListDTO> getFilteredCardsWithDTOForUser(CardFilterDTO filters, Integer tokenUserId) {
        List<CardModel> cards = repo.findWithFilters(
            filters.getStatus(),
            filters.getType(),
            filters.getUserId(),
            filters.getAccountId(),
            filters.getNetwork()
        );
        return convertToCardListDTOForUser(cards, tokenUserId);
    }

    private List<CardListDTO> convertToCardListDTOForAdmin(List<CardModel> cards) {
        List<CardListDTO> dtoList = new ArrayList<>();
        for (CardModel card : cards) {
            CardListDTO dto = new CardListDTO();
            dto.setCardId(card.getId());
            dto.setCardNumber(maskCardNumber(card.getCardnumber()));
            dto.setCardexpiry(card.getCardexpiry());
            dto.setCardstatus(card.getCardstatus());
            dto.setAccountid(card.getAccountid());
            dto.setType(card.getType());
            dto.setUserId(card.getUserid());
            dtoList.add(dto);
        }
        return dtoList;
    }

    private List<CardListDTO> convertToCardListDTOForUser(List<CardModel> cards, Integer tokenUserId) {
        List<CardListDTO> dtoList = new ArrayList<>();
        for (CardModel card : cards) {
            CardListDTO dto = new CardListDTO();
            dto.setCardId(card.getId());
            
            if (tokenUserId != null && tokenUserId.equals(card.getUserid())) {
                dto.setCardNumber(card.getCardnumber());
            } else {
                dto.setCardNumber(maskCardNumber(card.getCardnumber()));
            }
            
            dto.setCardexpiry(card.getCardexpiry());
            dto.setCardstatus(card.getCardstatus());
            dto.setAccountid(card.getAccountid());
            dto.setType(card.getType());
            dto.setUserId(card.getUserid());
            dtoList.add(dto);
        }
        return dtoList;
    }

    public boolean canUserAccessCard(int cardId, int userId) {
        return isCardOwnedByUser(cardId, userId);
    }

    private boolean isCardOwnedByUser(int cardId, int userId) {
        CardModel card = repo.findById(cardId).orElse(null);
        return card != null && card.getUserid() == userId;
    }


    private String maskCardNumber(String cardNumber) {
        return "**** **** **** " + cardNumber.substring(cardNumber.length() - 4);
    }

    private String generateCardNumber() {
        StringBuilder cardNumber = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            cardNumber.append(random.nextInt(10));
        }
        return cardNumber.toString();
    }

    private String generateCVV() {
        int cvv = 100 + random.nextInt(900);
        return String.valueOf(cvv);
    }

    private String generateExpiry() {
        int month = 1 + random.nextInt(12);
        int year = Year.now().getValue() + 3 + random.nextInt(4);
        return String.format("%02d/%02d", month, year % 100);
    }
}
