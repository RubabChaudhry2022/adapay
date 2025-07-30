package com.fintech.authservice.repository;

import com.fintech.authservice.model.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface PermissionRepository extends JpaRepository<Permission, Long> {

	@Query("SELECT p FROM Permission p WHERE p.method = :method")
	List<Permission> findByMethod(@Param("method") String method);
}
