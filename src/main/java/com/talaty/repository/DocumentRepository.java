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
    List<Document> findByEkyc_Id(Long ekycId);
    List<Document> findByType(DocumentType type);
    List<Document> findByEkyc_IdAndType(Long ekycId, DocumentType type);
    boolean existsByEkyc_IdAndType(Long ekycId, DocumentType type);
}
