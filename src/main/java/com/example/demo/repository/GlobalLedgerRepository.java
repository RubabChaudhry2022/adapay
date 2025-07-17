package com.example.demo.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.model.GlobalLedgerEntry;

public interface GlobalLedgerRepository extends JpaRepository<GlobalLedgerEntry, Long> {
	List<GlobalLedgerEntry> findAllByOrderByCreatedAtDesc();
}
