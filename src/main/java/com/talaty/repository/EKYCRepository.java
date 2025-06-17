package com.talaty.repository;

import com.talaty.enums.ApplicationStatus;
import com.talaty.model.EKYC;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EKYCRepository extends JpaRepository<EKYC, Long> {
    Optional<EKYC> findByUser_Id(Long userId);
    List<EKYC> findByStatusIn(List<ApplicationStatus> statuses);
    List<EKYC> findByStatus(ApplicationStatus status);
    List<EKYC> findByStatusOrderBySubmittedAtDesc(ApplicationStatus status);
    @Query("SELECT e FROM EKYC e WHERE e.status = :status AND e.submittedAt IS NOT NULL ORDER BY e.submittedAt DESC")
    List<EKYC> findSubmittedByStatus(@Param("status") ApplicationStatus status);
}
