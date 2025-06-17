package com.talaty.service;

import com.talaty.dto.ApiResponse;
import com.talaty.dto.request.EKYCRequestDto;
import com.talaty.dto.response.DocumentResponseDto;
import com.talaty.dto.response.EKYCResponseDto;
import com.talaty.dto.response.MediaResponseDto;
import com.talaty.enums.ApplicationStatus;
import com.talaty.enums.DocumentType;
import com.talaty.mapper.EKYCMapper;
import com.talaty.mapper.UserMapper;
import com.talaty.model.Document;
import com.talaty.model.EKYC;
import com.talaty.model.Media;
import com.talaty.model.User;
import com.talaty.repository.EKYCRepository;
import com.talaty.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class EKYCService {

    private final EKYCRepository ekycRepository;
    private final UserRepository userRepository;
    private final ScoringService scoringService;
    private final NotificationService notificationService;
    private final EKYCMapper ekycMapper;
    private final UserMapper userMapper;

    public EKYCService(EKYCRepository ekycRepository,
                       UserRepository userRepository,
                       ScoringService scoringService,
                       NotificationService notificationService,
                       EKYCMapper ekycMapper,
                       UserMapper userMapper) {
        this.ekycRepository = ekycRepository;
        this.userRepository = userRepository;
        this.scoringService = scoringService;
        this.notificationService = notificationService;
        this.ekycMapper = ekycMapper;
        this.userMapper = userMapper;
    }

    public ApiResponse<EKYCResponseDto> createOrUpdateEKYC(Long userId, EKYCRequestDto request) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

            EKYC ekyc = ekycRepository.findByUser_Id(userId).orElse(new EKYC());

            // Utiliser le mapper pour mettre à jour l'entité
            if (ekyc.getId() == null) {
                ekyc = ekycMapper.toEntity(request);
                ekyc.setStatus(ApplicationStatus.DRAFT);
            } else {
                ekycMapper.partialUpdate(request, ekyc);
            }

            // Lier à l'utilisateur si nouveau
            if (ekyc.getUser() == null) {
                ekyc.setUser(user);
            }

            // Calculer le score initial
            scoringService.calculateInitialScore(ekyc);

            // Sauvegarder
            EKYC savedEkyc = ekycRepository.save(ekyc);
            user.setEkyc(savedEkyc);
            userRepository.save(user);

            // Mettre à jour le profil completion
            updateProfileCompletion(user);

            log.info("eKYC créé/mis à jour pour l'utilisateur: {}", userId);
            return ApiResponse.success(ekycMapper.toDto(savedEkyc), "eKYC créé/mis à jour avec succès");

        } catch (Exception e) {
            log.error("Erreur lors de la création/mise à jour de l'eKYC", e);
            return ApiResponse.error("Erreur lors de la création/mise à jour de l'eKYC: " + e.getMessage());
        }
    }

    public ApiResponse<EKYCResponseDto> submitEKYC(Long userId, Long ekycId) {
        try {
            EKYC ekyc = ekycRepository.findById(ekycId)
                    .orElseThrow(() -> new RuntimeException("eKYC non trouvé"));

            // Vérifier propriétaire
            if (!ekyc.getUser().getId().equals(userId)) {
                return ApiResponse.error("Accès non autorisé à cet eKYC");
            }

            // Vérifier si déjà soumis
            if (ekyc.getStatus() != ApplicationStatus.DRAFT) {
                return ApiResponse.error("Cet eKYC a déjà été soumis");
            }

            // Vérifier documents requis
            if (!ekyc.areRequiredDocumentsSubmitted()) {
                return ApiResponse.error("Documents requis manquants");
            }

            // Changer statut et marquer soumission
            ekyc.setStatus(ApplicationStatus.PENDING);
            ekyc.setSubmittedAt(LocalDateTime.now());

            // Recalculer score final
            scoringService.calculateFinalScore(ekyc);

            EKYC savedEkyc = ekycRepository.save(ekyc);

            // Notification pour l'utilisateur
            notificationService.createNotification(
                    userId,
                    "eKYC soumis",
                    "Votre demande eKYC a été soumise avec succès et est en cours d'examen.",
                    "EKYC_SUBMITTED"
            );

            log.info("eKYC soumis pour l'utilisateur: {}", userId);
            return ApiResponse.success(ekycMapper.toDto(savedEkyc), "eKYC soumis avec succès");

        } catch (Exception e) {
            log.error("Erreur lors de la soumission de l'eKYC", e);
            return ApiResponse.error("Erreur lors de la soumission: " + e.getMessage());
        }
    }

    public ApiResponse<EKYCResponseDto> getMyEKYC(Long userId) {
        try {
            EKYC ekyc = ekycRepository.findByUser_Id(userId)
                    .orElseThrow(() -> new RuntimeException("Aucun eKYC trouvé pour cet utilisateur"));

            return ApiResponse.success(ekycMapper.toDto(ekyc), "eKYC récupéré avec succès");

        } catch (Exception e) {
            log.error("Erreur lors de la récupération de l'eKYC", e);
            return ApiResponse.error("Erreur lors de la récupération: " + e.getMessage());
        }
    }

    private void updateProfileCompletion(User user) {
        double completion = 0.0;
        EKYC ekyc = user.getEkyc();

        if (ekyc != null) {
            // Informations de base (40%)
            int basicFields = 0;
            int totalBasicFields = 10;

            if (ekyc.getCompanyName() != null && !ekyc.getCompanyName().trim().isEmpty()) basicFields++;
            if (ekyc.getBusinessSector() != null && !ekyc.getBusinessSector().trim().isEmpty()) basicFields++;
            if (ekyc.getBusinessPurpose() != null && !ekyc.getBusinessPurpose().trim().isEmpty()) basicFields++;
            if (ekyc.getAddress() != null && !ekyc.getAddress().trim().isEmpty()) basicFields++;
            if (ekyc.getCity() != null && !ekyc.getCity().trim().isEmpty()) basicFields++;
            if (ekyc.getCompanyRegistrationNumber() != null) basicFields++;
            if (ekyc.getBankAccountNumber() != null) basicFields++;
            if (ekyc.getBankName() != null) basicFields++;
            if (ekyc.getMonthlyRevenue() != null) basicFields++;
            if (ekyc.getRequestedCreditAmount() != null) basicFields++;

            completion += (basicFields * 40.0) / totalBasicFields;

            // Documents (40%)
            if (ekyc.isDocumentSubmitted(DocumentType.NATIONAL_ID)) completion += 10;
            if (ekyc.isDocumentSubmitted(DocumentType.COMPANY_REGISTRATION)) completion += 15;
            if (ekyc.getDocuments().stream().filter(d -> d.getType() == DocumentType.BANK_STATEMENT).count() >= 3) {
                completion += 15;
            }

            // Vérifications (20%)
            if (user.isEmailVerified()) completion += 10;
            if (user.isPhoneVerified()) completion += 10;
        }

        user.setProfileCompletion(Math.min(completion, 100.0));
        userRepository.save(user);
    }
}