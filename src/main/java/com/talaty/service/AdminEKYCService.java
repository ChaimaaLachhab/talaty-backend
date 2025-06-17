package com.talaty.service;

import com.talaty.dto.ApiResponse;
import com.talaty.dto.request.AdminReviewDto;
import com.talaty.dto.response.EKYCResponseDto;
import com.talaty.enums.ApplicationStatus;
import com.talaty.mapper.AdminReviewMapper;
import com.talaty.mapper.EKYCMapper;
import com.talaty.model.Document;
import com.talaty.model.EKYC;
import com.talaty.repository.DocumentRepository;
import com.talaty.repository.EKYCRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class AdminEKYCService {

    private final EKYCRepository ekycRepository;
    private final DocumentRepository documentRepository;
    private final NotificationService notificationService;
    private final EKYCMapper ekycMapper;
    private final AdminReviewMapper adminReviewMapper;

    public AdminEKYCService(EKYCRepository ekycRepository,
                            DocumentRepository documentRepository,
                            NotificationService notificationService,
                            EKYCMapper ekycMapper,
                            AdminReviewMapper adminReviewMapper) {
        this.ekycRepository = ekycRepository;
        this.documentRepository = documentRepository;
        this.notificationService = notificationService;
        this.ekycMapper = ekycMapper;
        this.adminReviewMapper = adminReviewMapper;
    }

    public ApiResponse<List<EKYCResponseDto>> getPendingEKYCs() {
        try {
            List<EKYC> pendingEKYCs = ekycRepository.findSubmittedByStatus(ApplicationStatus.PENDING);
            List<EKYCResponseDto> response = pendingEKYCs.stream()
                    .map(ekycMapper::toDto)
                    .collect(Collectors.toList());

            return ApiResponse.success(response, "eKYCs en attente récupérés");

        } catch (Exception e) {
            log.error("Erreur lors de la récupération des eKYCs en attente", e);
            return ApiResponse.error("Erreur: " + e.getMessage());
        }
    }

    public ApiResponse<List<EKYCResponseDto>> getEKYCsByStatus(ApplicationStatus status) {
        try {
            List<EKYC> ekycs = ekycRepository.findByStatusOrderBySubmittedAtDesc(status);
            List<EKYCResponseDto> response = ekycs.stream()
                    .map(ekycMapper::toDto)
                    .collect(Collectors.toList());

            return ApiResponse.success(response, "eKYCs récupérés par statut");

        } catch (Exception e) {
            log.error("Erreur lors de la récupération des eKYCs par statut", e);
            return ApiResponse.error("Erreur: " + e.getMessage());
        }
    }

    public ApiResponse<EKYCResponseDto> reviewEKYC(Long adminId, Long ekycId, AdminReviewDto reviewDto) {
        try {
            EKYC ekyc = ekycRepository.findById(ekycId)
                    .orElseThrow(() -> new RuntimeException("eKYC non trouvé"));

            // Vérifier statut
            if (ekyc.getStatus() != ApplicationStatus.PENDING && ekyc.getStatus() != ApplicationStatus.UNDER_REVIEW) {
                return ApiResponse.error("Cet eKYC ne peut plus être modifié");
            }

            // Utiliser le mapper pour appliquer la review
            adminReviewMapper.updateEKYCFromReview(reviewDto, ekyc);
            ekyc.setReviewedBy(adminId);

            EKYC savedEkyc = ekycRepository.save(ekyc);

            // Créer notification pour l'utilisateur
            String notificationTitle = reviewDto.getDecision() == ApplicationStatus.APPROVED
                    ? "eKYC approuvé"
                    : "eKYC rejeté";

            String notificationMessage = reviewDto.getDecision() == ApplicationStatus.APPROVED
                    ? "Félicitations ! Votre demande eKYC a été approuvée. Vous pouvez maintenant accéder à nos services."
                    : "Votre demande eKYC a été rejetée. " + (reviewDto.getComments() != null ? reviewDto.getComments() : "");

            notificationService.createNotification(
                    ekyc.getUser().getId(),
                    notificationTitle,
                    notificationMessage,
                    "EMAIL" // Envoi par email pour les décisions importantes
            );

            log.info("eKYC {} reviewé par admin {} avec décision: {}", ekycId, adminId, reviewDto.getDecision());
            return ApiResponse.success(ekycMapper.toDto(savedEkyc), "eKYC traité avec succès");

        } catch (Exception e) {
            log.error("Erreur lors du traitement de l'eKYC", e);
            return ApiResponse.error("Erreur lors du traitement: " + e.getMessage());
        }
    }

    public ApiResponse<String> verifyDocument(Long adminId, Long documentId, boolean verified, String notes) {
        try {
            Document document = documentRepository.findById(documentId)
                    .orElseThrow(() -> new RuntimeException("Document non trouvé"));

            document.setVerified(verified);
            document.setProcessingNotes(notes);
            document.setProcessedAt(LocalDateTime.now());
            document.setProcessedBy(adminId);

            documentRepository.save(document);

            log.info("Document {} vérifié par admin {}: {}", documentId, adminId, verified);
            return ApiResponse.success("", "Document vérifié avec succès");

        } catch (Exception e) {
            log.error("Erreur lors de la vérification du document", e);
            return ApiResponse.error("Erreur lors de la vérification: " + e.getMessage());
        }
    }
}