package com.example.Card_Service_V2.repositories;

import com.example.Card_Service_V2.models.CardModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CardRepo extends JpaRepository<CardModel, Integer> {

    @Query("SELECT c FROM CardModel c WHERE c.accountid = ?1")
    CardModel findByuseridid(int accountid);

    List<CardModel> findAllByAccountid(int accountid);

    Optional<CardModel> findByAccountid(int accountid);

    List<CardModel> findByCardstatus(String status);

    List<CardModel> findByuserid(int userId);

    Optional<CardModel> findByCardnumber(String cardNumber);

    @Query("SELECT c FROM CardModel c WHERE " +
           "(:status IS NULL OR c.cardstatus = :status) AND " +
           "(:type IS NULL OR c.type = :type) AND " +
           "(:userId IS NULL OR c.userid = :userId) AND " +
           "(:accountId IS NULL OR c.accountid = :accountId) AND " +
           "(:network IS NULL OR c.Network = :network)")
    List<CardModel> findWithFilters(
        @Param("status") String status,
        @Param("type") String type,
        @Param("userId") Integer userId,
        @Param("accountId") Integer accountId,
        @Param("network") String network);

    List<CardModel> findByCardstatusAndType(String status, String type);
    
    List<CardModel> findByUseridAndCardstatus(int userId, String status);
    
    List<CardModel> findByTypeAndCardstatus(String type, String status);
}

