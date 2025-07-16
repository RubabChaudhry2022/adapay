package com.example.Card_Service_V2.services;

import com.example.Card_Service_V2.models.CardModel;
import com.example.Card_Service_V2.repositories.CardRepo;
import com.example.Card_Service_V2.services.dtos.CardListDTO;
import com.example.Card_Service_V2.services.dtos.CreateCardDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class CardService {

    @Autowired
    private CardRepo repo;

    private Random random = new Random();

    public CreateCardDTO createCard( int userId, int accid, CardModel card) {
        card.setCardnumber(generateCardNumber());
        card.setCardcvv(generateCVV());
        card.setCardexpiry(generateExpiry());
        card.setCardpin(generatePin());

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

        System.out.println("userId = " + userId + ", accid = " + accid + ", card = " + response.toString());
        return response;
    }

    public String maskCardNumber(String cardNumber) {
        return "**** **** **** " + cardNumber.substring(cardNumber.length() - 4);
    }

    private String generateCardNumber() {
        StringBuilder cardNumber = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            cardNumber.append(random.nextInt(10));
        }
        return cardNumber.toString();
    }
    public CardModel getcard(int accids) {
        return repo.findByAccountid(accids)
                .orElseThrow(() -> new RuntimeException("Card not found with Account ID: " ));
    }

    private String generateCVV() {
        int cvv = 100 + random.nextInt(900);
        return String.valueOf(cvv);
    }

    private String generatePin() {
        int pin = 1000 + random.nextInt(9000);
        return String.valueOf(pin);
    }

    private String generateExpiry() {
        int month = 1 + random.nextInt(12);
        int year = Year.now().getValue() + 3 + random.nextInt(4);
        return String.format("%02d/%02d", month, year % 100);
    }

    public List<CardModel> getcards(CardModel card) {
        return repo.findAll();
    }

    public CardModel blockcard(int id) {
        CardModel card = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Card not found with ID: " + id));
        card.setCardstatus("Blocked");
        return repo.save(card);
    }

    public CardModel getcardbyid( int accid) {
        return repo.findByAccountid( accid)
                .orElseThrow(() -> new RuntimeException("Card not found with ID: "  + " and Account ID: " + accid));
    }


    public CardModel changepin(int id ,  String pin) {
        CardModel card = repo.findById( id).get();
        card.setCardpin(pin);
        System.out.println("accid = " + id + ", pin = " + pin);
        return repo.save(card);


    }

    public void freezecard(int accountid , String status) {
        CardModel card = repo.findById(accountid).get();
        card.setCardstatus(status);
        repo.save(card);
    }

    public List<CardListDTO> getCards() {
        List<CardModel> cards = repo.findAll();

        List<CardListDTO> dtoList = new ArrayList<>();
        for (CardModel card : cards) {
            CardListDTO dto = new CardListDTO();
            dto.setCardId(card.getId());
            dto.setMaskedCardNumber(maskCardNumber(card.getCardnumber()));
            dto.setCardexpiry(card.getCardexpiry());
            dto.setCardstatus(card.getCardstatus());
            dto.setAccountid(card.getAccountid());
            dto.setType(card.getType());
            dtoList.add(dto);
        }

        return dtoList;
    }


    public void findbyuser(int userid) {
        repo.findByuseridid(userid);
        System.out.println("Card found");

    }

    public boolean isAccountOwnedByUser(int accountId, int userId) {

        List<CardModel> userCards = repo.findByuserid(userId);
        return userCards.stream().anyMatch(card -> card.getAccountid() == accountId);

    }
  public boolean isCardOwnedByUser(int cardId, int userId) {
    System.out.println("Checking if card " + cardId + " is owned by user " + userId);
    CardModel card = repo.findById(cardId).orElse(null);
    System.out.println(card);
    System.out.println("Card user ID: " + (card != null ? card.getUserid() : "null"));
    return card != null && card.getUserid() == userId;


}

        

    public List<CardModel> getAllCardsByAccountId(int accountId) {
        List<CardModel> cards = repo.findAllByAccountid(accountId);
        return cards;
    }

    public List<CardModel> findByUser(int userId) {
        List<CardModel> cards = repo.findByuserid(userId);
        return cards;
    }

    public CardModel unblockCard(int cardId) {
         CardModel card = repo.findById(cardId)
                .orElseThrow(() -> new RuntimeException("Card not found with ID: " + cardId));
        card.setCardstatus("Active");
        return repo.save(card);
    }
}
