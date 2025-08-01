package com.example.demo.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.example.demo.model.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

	Page<Payment> findBySenderAccountIdOrReceiverAccountId(Long senderId, Long receiverId, Pageable pageable);

	boolean existsByTransactionRefId(String transactionRefId);

	List<Payment> findBySenderAccountIdOrReceiverAccountIdOrderByCreatedAtDesc(Long senderId, Long receiverId);

	@Query("SELECT p FROM Payment p WHERE "
			+ "(:accountId IS NULL OR :accountId IN (p.senderAccountId, p.receiverAccountId))")
	Page<Payment> findByAccountInvolved(@Param("accountId") Long accountId, Pageable pageable);

}