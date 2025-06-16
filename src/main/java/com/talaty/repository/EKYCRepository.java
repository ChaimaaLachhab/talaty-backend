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
    Optional<EKYC> findByNationalId(String nationalId);
    Optional<EKYC> findByCompanyRegistrationNumber(String registrationNumber);

    List<EKYC> findByStatus(ApplicationStatus status);
    List<EKYC> findByStatusOrderBySubmittedAtDesc(ApplicationStatus status);

    @Query("SELECT e FROM EKYC e WHERE e.score >= :minScore")
    List<EKYC> findByScoreGreaterThanEqual(@Param("minScore") Integer minScore);

    @Query("SELECT e FROM EKYC e WHERE e.submittedAt BETWEEN :startDate AND :endDate")
    List<EKYC> findBySubmittedAtBetween(@Param("startDate") LocalDateTime startDate,
                                        @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(e) FROM EKYC e WHERE e.status = :status")
    Long countByStatus(@Param("status") ApplicationStatus status);

    @Query("SELECT AVG(e.score) FROM EKYC e WHERE e.status = 'APPROVED'")
    Double getAverageScoreForApproved();
}
