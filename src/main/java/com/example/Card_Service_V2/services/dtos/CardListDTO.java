package com.example.Card_Service_V2.services.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
//for Admin viewing All Cards
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CardListDTO {
    private int cardId;
    private String maskedCardNumber;
    private String cardexpiry;
    private int accountid;
    private String type;
    private String cardstatus;

}
