package com.talaty.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.talaty.dto.ApiResponse;
import com.talaty.dto.request.DocumentUploadDto;
import com.talaty.dto.response.DocumentResponseDto;
import com.talaty.mapper.DocumentMapper;
import com.talaty.model.Document;
import com.talaty.model.EKYC;
import com.talaty.model.Media;
import com.talaty.repository.DocumentRepository;
import com.talaty.repository.EKYCRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final EKYCRepository ekycRepository;
    private final MediaService mediaService;
    private final DocumentMapper documentMapper;

    public DocumentService(DocumentRepository documentRepository,
                           EKYCRepository ekycRepository,
                           MediaService mediaService,
                           DocumentMapper documentMapper) {
        this.documentRepository = documentRepository;
        this.ekycRepository = ekycRepository;
        this.mediaService = mediaService;
        this.documentMapper = documentMapper;
    }

    public ApiResponse<DocumentResponseDto> uploadDocument(Long userId, DocumentUploadDto request,
                                                           List<MultipartFile> files) {
        try {
            // Vérifier eKYC
            EKYC ekyc = ekycRepository.findById(request.getEkycId())
                    .orElseThrow(() -> new RuntimeException("eKYC non trouvé"));

            if (!ekyc.getUser().getId().equals(userId)) {
                return ApiResponse.error("Accès non autorisé à cet eKYC");
            }

            // Créer ou récupérer document existant
            Document document = documentRepository.findByEkyc_IdAndType(request.getEkycId(), request.getDocumentType())
                    .stream().findFirst()
                    .orElse(new Document());

            // Mapper les données du DTO vers l'entité
            if (document.getId() == null) {
                document = documentMapper.toEntity(request);
                document.setEkyc(ekyc);
            } else {
                documentMapper.partialUpdate(request, document);
            }

            // Sauvegarder document
            Document savedDocument = documentRepository.save(document);

            // Upload des fichiers via MediaService existant
            List<Media> uploadedMedia = mediaService.addMediaToDocument(files, savedDocument.getId());

            // Simulation extraction de données pour chaque fichier
            for (Media media : uploadedMedia) {
                extractDataFromFile(savedDocument, media);
            }

            documentRepository.save(savedDocument);
            log.info("Document {} uploadé pour eKYC {}", request.getDocumentType(), request.getEkycId());

            return ApiResponse.success(documentMapper.toDto(savedDocument), "Document uploadé avec succès");

        } catch (Exception e) {
            log.error("Erreur lors de l'upload du document", e);
            return ApiResponse.error("Erreur lors de l'upload: " + e.getMessage());
        }
    }

    public ApiResponse<List<DocumentResponseDto>> getDocumentsByEKYC(Long userId, Long ekycId) {
        try {
            // Vérifier propriétaire
            EKYC ekyc = ekycRepository.findById(ekycId)
                    .orElseThrow(() -> new RuntimeException("eKYC non trouvé"));

            if (!ekyc.getUser().getId().equals(userId)) {
                return ApiResponse.error("Accès non autorisé");
            }

            List<Document> documents = documentRepository.findByEkyc_Id(ekycId);
            List<DocumentResponseDto> documentsDto = documents.stream()
                    .map(documentMapper::toDto)
                    .collect(Collectors.toList());

            return ApiResponse.success(documentsDto, "Documents récupérés avec succès");

        } catch (Exception e) {
            log.error("Erreur lors de la récupération des documents", e);
            return ApiResponse.error("Erreur lors de la récupération: " + e.getMessage());
        }
    }

    private void extractDataFromFile(Document document, Media media) {
        // Simulation extraction de données selon le type
        Map<String, Object> extractedData = new HashMap<>();

        switch (document.getType()) {
            case NATIONAL_ID:
                extractedData.put("documentType", "National ID");
                extractedData.put("idNumber", "K" + System.currentTimeMillis() % 10000000);
                extractedData.put("fullName", "TEMSAMANI Mouhcine");
                extractedData.put("birthDate", "1988-11-29");
                extractedData.put("expiryDate", "2029-09-09");
                break;

            case COMPANY_REGISTRATION:
                extractedData.put("documentType", "Company Registration");
                extractedData.put("companyName", document.getEkyc().getCompanyName());
                extractedData.put("registrationNumber", "RC" + System.currentTimeMillis() % 1000000);
                extractedData.put("establishedDate", LocalDate.now().minusYears(2));
                extractedData.put("legalForm", "SARL");
                break;

            case BANK_STATEMENT:
                extractedData.put("documentType", "Bank Statement");
                extractedData.put("bankName", document.getEkyc().getBankName());
                extractedData.put("accountNumber", document.getEkyc().getBankAccountNumber());
                extractedData.put("statementMonth", LocalDate.now().getMonth());
                extractedData.put("averageBalance", 50000 + (System.currentTimeMillis() % 100000));
                extractedData.put("transactions", Arrays.asList(
                        Map.of("date", "2025-01-15", "amount", 15000, "type", "CREDIT"),
                        Map.of("date", "2025-01-20", "amount", -5000, "type", "DEBIT")
                ));
                break;

            default:
                extractedData.put("documentType", document.getType().toString());
                extractedData.put("fileName", media.getOriginalFileName());
                break;
        }

        // Convertir en JSON et sauvegarder
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            String jsonData = objectMapper.writeValueAsString(extractedData);

            document.setExtractedData(jsonData);
            document.setDataExtracted(true);
            document.setDataExtractedAt(LocalDateTime.now());

        } catch (Exception e) {
            log.error("Erreur extraction données: {}", e.getMessage());
        }
    }
}