package com.example.Card_Service.repositories;

import com.example.Card_Service.models.CardModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CardRepo extends JpaRepository<CardModel, Integer> {
}
