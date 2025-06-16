package com.talaty.repository;

import com.talaty.enums.DocumentType;
import com.talaty.model.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {
    List<Document> findByEkycId(Long ekycId);
    List<Document> findByType(DocumentType type);
    List<Document> findByVerified(boolean verified);
    List<Document> findByRequired(boolean required);

    @Query("SELECT d FROM Document d WHERE d.ekyc.id = :ekycId AND d.type = :type")
    Optional<Document> findByEkycIdAndType(@Param("ekycId") Long ekycId,
                                           @Param("type") DocumentType type);

    @Query("SELECT d FROM Document d WHERE d.ekyc.id = :ekycId AND d.verified = false")
    List<Document> findUnverifiedByEkycId(@Param("ekycId") Long ekycId);

    @Query("SELECT COUNT(d) FROM Document d WHERE d.ekyc.id = :ekycId AND d.verified = true")
    Long countVerifiedByEkycId(@Param("ekycId") Long ekycId);

    @Query("SELECT COUNT(d) FROM Document d WHERE d.ekyc.id = :ekycId AND d.required = true")
    Long countRequiredByEkycId(@Param("ekycId") Long ekycId);
}
