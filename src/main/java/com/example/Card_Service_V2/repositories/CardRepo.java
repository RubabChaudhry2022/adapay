package com.example.Card_Service_V2.repositories;

import com.example.Card_Service_V2.models.CardModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CardRepo extends JpaRepository<CardModel, Integer> {

    @Query("SELECT c FROM CardModel c WHERE c.accountid = ?1")
    CardModel findByuseridid(int accountid);

    // Return all cards for a specific account ID
    List<CardModel> findAllByAccountid(int accountid);

    // Keep the original method for backward compatibility
    Optional<CardModel> findByAccountid(int accountid);

    List<CardModel> findByCardstatus(String status);

    List<CardModel> findByuserid(int userId);


}

