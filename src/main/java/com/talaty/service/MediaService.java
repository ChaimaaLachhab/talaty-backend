package com.talaty.service;

import com.talaty.enums.MediaType;
import com.talaty.exception.FileUploadException;
import com.talaty.model.Document;
import com.talaty.model.Media;
import com.talaty.model.User;
import com.talaty.repository.DocumentRepository;
import com.talaty.repository.MediaRepository;
import com.talaty.util.FileUploadUtil;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MediaService {
    private final MediaUploadService mediaUploadService;
    private final MediaRepository mediaRepository;
    private final DocumentRepository documentRepository;

    @Autowired
    public MediaService(MediaUploadService mediaUploadService,
                        MediaRepository mediaRepository,
                        DocumentRepository documentRepository) {
        this.mediaUploadService = mediaUploadService;
        this.mediaRepository = mediaRepository;
        this.documentRepository = documentRepository;
    }

    /**
     * Update or create user photo
     */
    public Media updateMediaForUser(MultipartFile userPhoto, User user) {
        Media existingMedia = user.getUserPhoto();

        try {
            if (existingMedia != null) {
                // Delete old file from Cloudinary
                mediaUploadService.deleteMediaFile(existingMedia);

                // Upload new file and update existing media entity
                Media newMediaData = mediaUploadService.handleMediaUpload(userPhoto, user);

                existingMedia.setMediaUrl(newMediaData.getMediaUrl());
                existingMedia.setMediaId(newMediaData.getMediaId());
                existingMedia.setOriginalFileName(newMediaData.getOriginalFileName());
                existingMedia.setMimeType(newMediaData.getMimeType());
                existingMedia.setFileSize(newMediaData.getFileSize());
                existingMedia.setMediaType(MediaType.IMAGE);
                existingMedia.setUploadedAt(LocalDateTime.now());

                return mediaRepository.save(existingMedia);
            } else {
                // Create new media
                Media newMedia = mediaUploadService.handleMediaUpload(userPhoto, user);
                newMedia.setMediaType(MediaType.IMAGE);
                Media savedMedia = mediaRepository.save(newMedia);
                user.setUserPhoto(savedMedia);
                return savedMedia;
            }
        } catch (Exception e) {
            throw new FileUploadException("Failed to update user photo: " + e.getMessage(), e);
        }
    }

    /**
     * Add multiple media files to a document
     */
    public List<Media> addMediaToDocument(List<MultipartFile> files, Long documentId) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new EntityNotFoundException("Document not found with id: " + documentId));

        List<Media> uploadedMedia = mediaUploadService.handleMultipleMediaUpload(files, document);
        List<Media> savedMedia = mediaRepository.saveAll(uploadedMedia);

        // Update document's media list
        document.getMediaFiles().addAll(savedMedia);
        documentRepository.save(document);

        return savedMedia;
    }

    /**
     * Add single media file to a document
     */
    public Media addSingleMediaToDocument(MultipartFile file, Long documentId) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new EntityNotFoundException("Document not found with id: " + documentId));

        Media media = mediaUploadService.handleMediaUpload(file, document);
        Media savedMedia = mediaRepository.save(media);

        document.getMediaFiles().add(savedMedia);
        documentRepository.save(document);

        return savedMedia;
    }

    /**
     * Remove media from document
     */
    public void removeMediaFromDocument(Long mediaId, Long documentId) {
        Media media = mediaRepository.findById(mediaId)
                .orElseThrow(() -> new EntityNotFoundException("Media not found with id: " + mediaId));

        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new EntityNotFoundException("Document not found with id: " + documentId));

        if (!media.getDocument().getId().equals(documentId)) {
            throw new IllegalStateException("Media does not belong to this document");
        }

        // Remove from document
        document.getMediaFiles().remove(media);

        // Delete media file and entity
        mediaUploadService.deleteMediaFile(media);
        mediaRepository.delete(media);

        documentRepository.save(document);
    }

    /**
     * Replace specific media in document
     */
    public Media replaceMediaInDocument(MultipartFile newFile, Long mediaId, Long documentId) {
        Media existingMedia = mediaRepository.findById(mediaId)
                .orElseThrow(() -> new EntityNotFoundException("Media not found with id: " + mediaId));

        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new EntityNotFoundException("Document not found with id: " + documentId));

        if (!existingMedia.getDocument().getId().equals(documentId)) {
            throw new IllegalStateException("Media does not belong to this document");
        }

        try {
            // Delete old file from Cloudinary
            mediaUploadService.deleteMediaFile(existingMedia);

            // Upload new file
            Media newMediaData = mediaUploadService.handleMediaUpload(newFile, document);

            // Update existing media entity
            existingMedia.setMediaUrl(newMediaData.getMediaUrl());
            existingMedia.setMediaId(newMediaData.getMediaId());
            existingMedia.setOriginalFileName(newMediaData.getOriginalFileName());
            existingMedia.setMimeType(newMediaData.getMimeType());
            existingMedia.setFileSize(newMediaData.getFileSize());
            existingMedia.setMediaType(FileUploadUtil.determineFileType(newFile.getContentType()));
            existingMedia.setUploadedAt(LocalDateTime.now());

            return mediaRepository.save(existingMedia);
        } catch (Exception e) {
            throw new FileUploadException("Failed to replace media: " + e.getMessage(), e);
        }
    }

    /**
     * Get all media for a document
     */
    public List<Media> getDocumentMedia(Long documentId) {
        return mediaRepository.findByDocumentIdOrderByUploadedAtDesc(documentId);
    }

    /**
     * Get media by mediaType for a document
     */
    public List<Media> getDocumentMediaByType(Long documentId, MediaType mediaType) {
        return mediaRepository.findByDocumentIdAndTypeOrderByUploadedAtDesc(documentId, mediaType);
    }
}
