package com.talaty.repository;

import com.talaty.enums.ApplicationStatus;
import com.talaty.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByUsername(String username);
    Optional<Customer> findByEmail(String email);

    @Query("SELECT c FROM Customer c WHERE c.ekyc IS NOT NULL")
    List<Customer> findAllWithEKYC();

    @Query("SELECT c FROM Customer c WHERE c.ekyc.status = :status")
    List<Customer> findByEKYCStatus(@Param("status") ApplicationStatus status);
}
