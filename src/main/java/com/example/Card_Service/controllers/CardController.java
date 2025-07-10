package com.example.Card_Service.controllers;

import com.example.Card_Service.models.CardModel;
import com.example.Card_Service.services.CardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController




        public class CardController {
    @Autowired
    CardService cardser;

    @PostMapping("/createcard/{id}/{accid}")
    public CardModel createcard(@PathVariable int id ,@PathVariable int accid,  @RequestBody CardModel card){

return cardser.createCard(id , accid ,card);

    }

    @GetMapping("/cards")
    public List<CardModel> cards ( CardModel card){
       return cardser.getcards(card);
    }
    @PostMapping("/blockcard/{id}")
    public CardModel blockcard (@PathVariable int id){
        return  cardser.blockcard(id);
    }


}


