package com.talaty.controller;

import com.talaty.dto.ApiResponse;
import com.talaty.dto.LoginResponse;
import com.talaty.dto.request.OTPRequestDto;
import com.talaty.dto.request.OTPVerifyDto;
import com.talaty.dto.response.OTPResponseDto;
import com.talaty.model.User;
import com.talaty.repository.UserRepository;
import com.talaty.service.AuthenticationService;
import com.talaty.service.JwtService;
import com.talaty.service.OTPService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/otp")
@Validated
public class OTPController {

    private final OTPService otpService;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final AuthenticationService authenticationService;

    public OTPController(OTPService otpService, JwtService jwtService, UserRepository userRepository, AuthenticationService authenticationService) {
        this.otpService = otpService;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.authenticationService = authenticationService;
    }

    @PostMapping("/send")
    public ResponseEntity<ApiResponse<OTPResponseDto>> sendOTP(@Valid @RequestBody OTPRequestDto request) {
        ApiResponse<OTPResponseDto> response = otpService.generateAndSendOTP(request.getPhone());

        return response.isSuccess()
                ? ResponseEntity.ok(response)
                : ResponseEntity.badRequest().body(response);
    }

    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<LoginResponse>> verifyOTP(@Valid @RequestBody OTPVerifyDto request) {
        try {
            ApiResponse<Boolean> verificationResult = otpService.verifyOTP(request.getPhone(), request.getOtpCode());

            if (!verificationResult.isSuccess() || !verificationResult.getData()) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("Code OTP invalide"));
            }

            // Récupérer l'utilisateur et générer JWT
            User user = userRepository.findByPhone(request.getPhone())
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

            String jwtToken = jwtService.generateToken(user);
            LoginResponse loginResponse = authenticationService.createAuthResponse(user, jwtToken);

            return ResponseEntity.ok(ApiResponse.success(loginResponse, "Connexion réussie via OTP"));

        } catch (Exception e) {
            log.error("Erreur lors de la vérification OTP", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Erreur lors de la vérification: " + e.getMessage()));
        }
    }
}

