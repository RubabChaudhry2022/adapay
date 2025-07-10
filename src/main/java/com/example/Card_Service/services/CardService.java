package com.example.Card_Service.services;

import com.example.Card_Service.models.CardModel;
import com.example.Card_Service.repositories.CardRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Year;
import java.util.List;
import java.util.Random;

@Service
public class CardService {

    @Autowired
    private CardRepo repo;

    private Random random = new Random();

    public CardModel createCard(int userId, int accid, CardModel card) {
        card.setCardnumber(generateCardNumber());
        card.setCardcvv(generateCVV());
        card.setCardexpiry(generateExpiry());
        card.setCardpin(generatePin());
        card.setCardstatus("ACTIVE");
        card.setUserId(userId);
        card.setAccountid(accid);

        return repo.save(card);
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
}
