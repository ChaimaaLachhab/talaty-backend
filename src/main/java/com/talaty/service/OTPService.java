package com.talaty.service;

import com.talaty.dto.ApiResponse;
import com.talaty.dto.response.OTPResponseDto;
import com.talaty.mapper.OTPMapper;
import com.talaty.model.User;
import com.talaty.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Slf4j
@Service
public class OTPService {

    private final UserRepository userRepository;
    private final OTPMapper otpMapper;
    private final EmailService emailService;

    public OTPService(UserRepository userRepository, OTPMapper otpMapper, EmailService emailService) {
        this.userRepository = userRepository;
        this.otpMapper = otpMapper;
        this.emailService = emailService;
    }

    public ApiResponse<OTPResponseDto> generateAndSendOTP(String phone) {
        try {
            // Rechercher l'utilisateur par téléphone
            User user = userRepository.findByPhone(phone)
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec ce numéro"));

            // Générer code OTP (6 chiffres)
            String otpCode = generateOTPCode();

            // Définir expiration (5 minutes)
            LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(5);

            // Sauvegarder OTP
            user.setOtpCode(otpCode);
            user.setOtpExpiryTime(expiryTime);
            userRepository.save(user);

            // Envoyer OTP par SMS ET email si disponible
            sendSMS(phone, otpCode);

            if (user.getEmail() != null && user.isEmailVerified()) {
                try {
                    emailService.sendOTPEmail(user.getEmail(), otpCode);
                } catch (Exception e) {
                    log.warn("Échec envoi OTP par email pour {}: {}", user.getEmail(), e.getMessage());
                }
            }

            log.info("OTP généré et envoyé pour le numéro: {}", phone);
            return ApiResponse.success(otpMapper.toSuccessResponse("Code OTP envoyé avec succès"), "OTP envoyé");

        } catch (Exception e) {
            log.error("Erreur lors de l'envoi de l'OTP", e);
            return ApiResponse.error("Erreur lors de l'envoi de l'OTP: " + e.getMessage());
        }
    }

    public ApiResponse<Boolean> verifyOTP(String phone, String otpCode) {
        try {
            User user = userRepository.findByPhone(phone)
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

            // Vérifier si OTP est valide
            if (user.getOtpCode() == null || user.getOtpExpiryTime() == null) {
                return ApiResponse.error("Aucun OTP actif trouvé");
            }

            // Vérifier expiration
            if (LocalDateTime.now().isAfter(user.getOtpExpiryTime())) {
                user.setOtpCode(null);
                user.setOtpExpiryTime(null);
                userRepository.save(user);
                return ApiResponse.error("Code OTP expiré");
            }

            // Vérifier le code
            boolean isValid = otpCode.equals(user.getOtpCode());

            if (isValid) {
                // Marquer téléphone comme vérifié et nettoyer OTP
                user.setPhoneVerified(true);
                user.setOtpCode(null);
                user.setOtpExpiryTime(null);
                userRepository.save(user);
                log.info("OTP vérifié avec succès pour: {}", phone);
                return ApiResponse.success(true, "OTP vérifié avec succès");
            } else {
                return ApiResponse.error("Code OTP invalide");
            }

        } catch (Exception e) {
            log.error("Erreur lors de la vérification de l'OTP", e);
            return ApiResponse.error("Erreur lors de la vérification: " + e.getMessage());
        }
    }

    private String generateOTPCode() {
        return String.format("%06d", new Random().nextInt(999999));
    }

    private void sendSMS(String phone, String otpCode) {
        // Simulation envoi SMS - en production, intégrer Twilio, AWS SNS, etc.
        log.info("📱 SMS envoyé à {} : Votre code Talaty est {}", phone, otpCode);

        // Pour les tests, on peut aussi stocker en base ou en cache
        // En production, remplacer par vraie intégration SMS
    }
}