package com.example.Card_Service_V2.services.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

//Response That will be sent after creating a card
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateCardDTO {
    private int cardId;
    private String maskedCardNumber;
    private String cardexpiry;
    private String type;
    private String cardstatus;
    private LocalDateTime createdAt;

}
