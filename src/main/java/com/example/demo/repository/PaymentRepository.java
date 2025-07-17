package com.example.demo.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.example.demo.model.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
	@Query("SELECT p FROM Payment p WHERE p.senderAccount.userId = :userId OR p.receiverAccount.userId = :userId")
	List<Payment> findByUserIdInSenderOrReceiver(@Param("userId") Long userId);

}