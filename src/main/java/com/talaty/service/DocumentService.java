package com.talaty.service;

import com.talaty.dto.ApiResponse;
import com.talaty.dto.request.DocumentUploadDto;
import com.talaty.dto.response.DocumentResponseDto;
import com.talaty.mapper.DocumentMapper;
import com.talaty.model.Customer;
import com.talaty.model.Document;
import com.talaty.model.EKYC;
import com.talaty.model.Media;
import com.talaty.repository.CustomerRepository;
import com.talaty.repository.DocumentRepository;
import com.talaty.repository.EKYCRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class DocumentService {

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private EKYCRepository ekycRepository;

    @Autowired
    private MediaService mediaService;

    @Autowired
    private EKYCService ekycService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private DocumentMapper documentMapper;

    @Autowired
    private CustomerRepository customerRepository;

    public ApiResponse<DocumentResponseDto> createDocument(Long ekycId, DocumentUploadDto request) {
        try {
            Optional<EKYC> ekycOpt = ekycRepository.findById(ekycId);
            if (ekycOpt.isEmpty()) {
                return ApiResponse.error("eKYC not found");
            }

            EKYC ekyc = ekycOpt.get();

            // Check if document of this type already exists
            Optional<Document> existingDoc = documentRepository.findByEkycIdAndType(ekycId, request.getType());
            if (existingDoc.isPresent()) {
                return ApiResponse.error("Document of this type already exists for this eKYC");
            }

            Document document = new Document();
            document.setType(request.getType());
            document.setName(request.getName());
            document.setDescription(request.getDescription());
            document.setEkyc(ekyc);

            document = documentRepository.save(document);

            DocumentResponseDto response = documentMapper.toDto(document);
            return ApiResponse.success("Document created successfully", response);

        } catch (Exception e) {
            log.error("Document creation failed", e);
            return ApiResponse.error("Document creation failed: " + e.getMessage());
        }
    }

    public ApiResponse<DocumentResponseDto> uploadDocumentFiles(Long documentId, List<MultipartFile> files) {
        try {
            Optional<Document> documentOpt = documentRepository.findById(documentId);
            if (documentOpt.isEmpty()) {
                return ApiResponse.error("Document not found");
            }

            Document document = documentOpt.get();

            // Use MediaService to upload multiple files to the document
            List<Media> uploadedMedia = mediaService.addMediaToDocument(files, documentId);

            if (uploadedMedia.isEmpty()) {
                return ApiResponse.error("No files were uploaded successfully");
            }

            // Refresh document to get updated media files
            document = documentRepository.findById(documentId).orElse(document);

            // Recalculate eKYC score
            ekycService.calculateAndUpdateScore(document.getEkyc().getId());

            DocumentResponseDto response = documentMapper.toDto(document);
            return ApiResponse.success("Files uploaded successfully", response);

        } catch (EntityNotFoundException e) {
            log.error("Entity not found during file upload", e);
            return ApiResponse.error("Document not found: " + e.getMessage());
        } catch (Exception e) {
            log.error("File upload failed", e);
            return ApiResponse.error("File upload failed: " + e.getMessage());
        }
    }

    public ApiResponse<DocumentResponseDto> uploadSingleDocumentFile(Long documentId, MultipartFile file) {
        try {
            Optional<Document> documentOpt = documentRepository.findById(documentId);
            if (documentOpt.isEmpty()) {
                return ApiResponse.error("Document not found");
            }

            Document document = documentOpt.get();

            // Use MediaService to upload single file to the document
            Media uploadedMedia = mediaService.addSingleMediaToDocument(file, documentId);

            // Refresh document to get updated media files
            document = documentRepository.findById(documentId).orElse(document);

            // Recalculate eKYC score
            ekycService.calculateAndUpdateScore(document.getEkyc().getId());

            DocumentResponseDto response = documentMapper.toDto(document);
            return ApiResponse.success("File uploaded successfully", response);

        } catch (EntityNotFoundException e) {
            log.error("Entity not found during file upload", e);
            return ApiResponse.error("Document not found: " + e.getMessage());
        } catch (Exception e) {
            log.error("File upload failed", e);
            return ApiResponse.error("File upload failed: " + e.getMessage());
        }
    }

    public ApiResponse<String> removeMediaFromDocument(Long documentId, Long mediaId) {
        try {
            mediaService.removeMediaFromDocument(mediaId, documentId);

            // Refresh document and recalculate eKYC score
            Optional<Document> documentOpt = documentRepository.findById(documentId);
            if (documentOpt.isPresent()) {
                ekycService.calculateAndUpdateScore(documentOpt.get().getEkyc().getId());
            }

            return ApiResponse.success("Media removed successfully");

        } catch (EntityNotFoundException e) {
            log.error("Entity not found during media removal", e);
            return ApiResponse.error("Document or media not found: " + e.getMessage());
        } catch (IllegalStateException e) {
            log.error("Illegal state during media removal", e);
            return ApiResponse.error(e.getMessage());
        } catch (Exception e) {
            log.error("Media removal failed", e);
            return ApiResponse.error("Media removal failed: " + e.getMessage());
        }
    }

    public ApiResponse<DocumentResponseDto> replaceMediaInDocument(Long documentId, Long mediaId, MultipartFile newFile) {
        try {
            Media replacedMedia = mediaService.replaceMediaInDocument(newFile, mediaId, documentId);

            // Refresh document and recalculate eKYC score
            Optional<Document> documentOpt = documentRepository.findById(documentId);
            if (documentOpt.isEmpty()) {
                return ApiResponse.error("Document not found");
            }

            Document document = documentOpt.get();
            ekycService.calculateAndUpdateScore(document.getEkyc().getId());

            DocumentResponseDto response = documentMapper.toDto(document);
            return ApiResponse.success("Media replaced successfully", response);

        } catch (EntityNotFoundException e) {
            log.error("Entity not found during media replacement", e);
            return ApiResponse.error("Document or media not found: " + e.getMessage());
        } catch (IllegalStateException e) {
            log.error("Illegal state during media replacement", e);
            return ApiResponse.error(e.getMessage());
        } catch (Exception e) {
            log.error("Media replacement failed", e);
            return ApiResponse.error("Media replacement failed: " + e.getMessage());
        }
    }

    public ApiResponse<List<DocumentResponseDto>> getDocumentsByEKYC(Long ekycId) {
        try {
            List<Document> documents = documentRepository.findByEkycId(ekycId);
            List<DocumentResponseDto> responses = documents.stream()
                    .map(documentMapper::toDto)
                    .collect(Collectors.toList());

            return ApiResponse.success(responses);

        } catch (Exception e) {
            log.error("Failed to get documents", e);
            return ApiResponse.error("Failed to get documents: " + e.getMessage());
        }
    }

    public ApiResponse<List<Media>> getDocumentMedia(Long documentId) {
        try {
            List<Media> mediaFiles = mediaService.getDocumentMedia(documentId);
            return ApiResponse.success("Media files retrieved successfully", mediaFiles);

        } catch (Exception e) {
            log.error("Failed to get document media", e);
            return ApiResponse.error("Failed to get document media: " + e.getMessage());
        }
    }

    public ApiResponse<DocumentResponseDto> verifyDocument(Long documentId, String verificationNotes, Long adminId) {
        try {
            Optional<Document> documentOpt = documentRepository.findById(documentId);
            if (documentOpt.isEmpty()) {
                return ApiResponse.error("Document not found");
            }

            Document document = documentOpt.get();
            document.setVerified(true);

            document = documentRepository.save(document);

            // Recalculate eKYC score
            ekycService.calculateAndUpdateScore(document.getEkyc().getId());

            // Find customer and send notification
            Document finalDocument = document;
            Optional<Customer> customerOpt = customerRepository.findAll().stream()
                    .filter(c -> c.getEkyc() != null && c.getEkyc().getId().equals(finalDocument.getEkyc().getId()))
                    .findFirst();

            if (customerOpt.isPresent()) {
                notificationService.createNotification(
                        customerOpt.get().getId(),
                        "Document Verified",
                        String.format("Your %s document has been verified.", document.getType().toString()),
                        "EMAIL"
                );
            }

            DocumentResponseDto response = documentMapper.toDto(document);
            return ApiResponse.success("Document verified successfully", response);

        } catch (Exception e) {
            log.error("Document verification failed", e);
            return ApiResponse.error("Document verification failed: " + e.getMessage());
        }
    }

    public ApiResponse<DocumentResponseDto> rejectDocument(Long documentId, String rejectionReason, Long adminId) {
        try {
            Optional<Document> documentOpt = documentRepository.findById(documentId);
            if (documentOpt.isEmpty()) {
                return ApiResponse.error("Document not found");
            }

            Document document = documentOpt.get();
            document.setVerified(false);

            document = documentRepository.save(document);

            // Recalculate eKYC score
            ekycService.calculateAndUpdateScore(document.getEkyc().getId());

            // Find customer and send notification
            Document finalDocument = document;
            Optional<Customer> customerOpt = customerRepository.findAll().stream()
                    .filter(c -> c.getEkyc() != null && c.getEkyc().getId().equals(finalDocument.getEkyc().getId()))
                    .findFirst();

            if (customerOpt.isPresent()) {
                notificationService.createNotification(
                        customerOpt.get().getId(),
                        "Document Rejected",
                        String.format("Your %s document has been rejected. Reason: %s",
                                document.getType().toString(), rejectionReason),
                        "EMAIL"
                );
            }

            DocumentResponseDto response = documentMapper.toDto(document);
            return ApiResponse.success("Document rejected successfully", response);

        } catch (Exception e) {
            log.error("Document rejection failed", e);
            return ApiResponse.error("Document rejection failed: " + e.getMessage());
        }
    }
}