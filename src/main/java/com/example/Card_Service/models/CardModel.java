package com.example.Card_Service.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class CardModel {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "card_model_generator")
    @SequenceGenerator(name = "card_model_generator", sequenceName = "card_model_new_seq", initialValue = 1, allocationSize = 1)
    int id;

    int userid;
    int accountid;
    String Network;
    String type;


    String cardnumber;
    String cardcvv;
    String cardexpiry;
    String cardpin;
    String cardstatus;

    public void setUserId(int userId) {
        this.userid = userId;
    }
}