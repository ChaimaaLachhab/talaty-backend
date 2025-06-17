package com.talaty.repository;

import com.talaty.enums.MediaType;
import com.talaty.model.Media;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MediaRepository extends JpaRepository<Media, Long> {
    List<Media> findByDocumentIdOrderByUploadedAtDesc(Long documentId);
    List<Media> findByMediaTypeAndDocumentId(MediaType mediaType, Long documentId);
    Optional<Media> findByMediaId(String mediaId);
    List<Media> findByDocumentIdAndMediaTypeOrderByUploadedAtDesc(Long documentId, MediaType mediaType);
}
