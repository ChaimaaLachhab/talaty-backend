package com.talaty.service;

import com.talaty.dto.ApiResponse;
import com.talaty.dto.LoginResponse;
import com.talaty.dto.LoginRequest;
import com.talaty.dto.request.AdminRequestDto;
import com.talaty.dto.request.CustomerRequestDto;
import com.talaty.dto.response.AdminResponseDto;
import com.talaty.dto.response.CustomerResponseDto;
import com.talaty.enums.ApplicationStatus;
import com.talaty.mapper.UserMapper;
import com.talaty.model.Admin;
import com.talaty.model.Customer;
import com.talaty.model.User;
import com.talaty.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@Transactional
public class AuthenticationService {

    @Value("${email.verification.expiration-hours:24}")
    private int emailVerificationExpirationHours;

    @Value("${otp.expiration-minutes:5}")
    private int otpExpirationMinutes;

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final OTPService otpService;
    private final EmailService emailService;
    private final NotificationService notificationService;

    @Autowired
    public AuthenticationService(UserRepository userRepository,
                                 UserMapper userMapper,
                                 PasswordEncoder passwordEncoder,
                                 AuthenticationManager authenticationManager,
                                 JwtService jwtService,
                                 EmailService emailService,
                                 NotificationService notificationService,
                                 OTPService otpService) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.otpService = otpService;
        this.emailService = emailService;
        this.notificationService = notificationService;
    }

    public ApiResponse<CustomerResponseDto> signup(CustomerRequestDto request) {
        try {
            // Vérifications d'unicité
            if (userRepository.findByUsername(request.getUsername()).isPresent()) {
                return ApiResponse.error("Nom d'utilisateur déjà existant");
            }
            if (userRepository.findByEmail(request.getEmail()).isPresent()) {
                return ApiResponse.error("Email déjà existant");
            }
            if (request.getPhone() != null && userRepository.findByPhone(request.getPhone()).isPresent()) {
                return ApiResponse.error("Numéro de téléphone déjà existant");
            }

            // Utiliser le mapper pour créer l'entité Customer
            Customer customer = userMapper.toCustomerEntity(request);
            customer.setPassword(passwordEncoder.encode(request.getPassword()));
            customer.setEmailVerificationToken(generateEmailVerificationToken());
            customer.setEmailVerificationExpiry(LocalDateTime.now().plusHours(emailVerificationExpirationHours));

            // Calcul initial du profil completion
            customer.setProfileCompletion(calculateInitialProfileCompletion(customer));

            Customer savedCustomer = userRepository.save(customer);

            // Envoi email de vérification
            try {
                emailService.sendVerificationEmail(savedCustomer.getEmail(), savedCustomer.getEmailVerificationToken());
            } catch (Exception e) {
                log.warn("Échec envoi email de vérification: {}", e.getMessage());
            }

            // Création notification de bienvenue
            notificationService.createNotification(
                    savedCustomer.getId(),
                    "Bienvenue sur Talaty !",
                    "Veuillez vérifier votre email pour compléter votre inscription.",
                    "EMAIL"
            );

            // Utiliser le mapper pour la réponse
            CustomerResponseDto responseDto = userMapper.toCustomerResponseDto(savedCustomer);
            return ApiResponse.success(responseDto, "Inscription réussie. Vérifiez votre email.");

        } catch (Exception e) {
            log.error("Échec de l'inscription", e);
            return ApiResponse.error("Échec de l'inscription: " + e.getMessage());
        }
    }

    public ApiResponse<AdminResponseDto> addAdmin(AdminRequestDto input) {
        try {
            // Vérifications d'unicité
            if (userRepository.findByUsername(input.getUsername()).isPresent()) {
                return ApiResponse.error("Nom d'utilisateur déjà existant");
            }
            if (userRepository.findByEmail(input.getEmail()).isPresent()) {
                return ApiResponse.error("Email déjà existant");
            }

            // Utiliser le mapper pour créer l'entité Admin
            Admin admin = userMapper.toAdminEntity(input);
            admin.setPassword(passwordEncoder.encode(input.getPassword()));
            admin.setProfileCompletion(100.0);
            admin.setEmailVerified(true); // Admin pré-vérifié
            admin.setVerified(true);

            Admin savedAdmin = userRepository.save(admin);

            // Notification de création admin
            notificationService.createNotification(
                    savedAdmin.getId(),
                    "Compte Administrateur créé",
                    "Votre compte administrateur Talaty a été créé avec succès.",
                    "EMAIL"
            );

            // Utiliser le mapper pour la réponse
            AdminResponseDto responseDto = userMapper.toAdminResponseDto(savedAdmin);
            return ApiResponse.success(responseDto, "Administrateur créé avec succès");

        } catch (Exception e) {
            log.error("Échec création admin", e);
            return ApiResponse.error("Échec création admin: " + e.getMessage());
        }
    }

    public ApiResponse<LoginResponse> authenticate(LoginRequest input) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            input.getUserNameOrEmail(),
                            input.getPassword()
                    )
            );

            User user = (User) authentication.getPrincipal();

            // Mise à jour du profil completion à chaque connexion
            updateProfileCompletion(user);
            userRepository.save(user);

            String token = jwtService.generateToken(user);
            LoginResponse response = createAuthResponse(user, token);

            log.info("Connexion réussie pour: {}", user.getUsername());
            return ApiResponse.success(response, "Connexion réussie");

        } catch (BadCredentialsException e) {
            log.error("Échec de connexion - mauvaises credentials", e);
            return ApiResponse.error("Identifiants incorrects");
        } catch (Exception e) {
            log.error("Échec de connexion", e);
            return ApiResponse.error("Échec de connexion: " + e.getMessage());
        }
    }

    public ApiResponse<LoginResponse> authenticateWithOTP(String phone, String otpCode) {
        try {
            // Utiliser le service OTP refactorisé
            ApiResponse<Boolean> verificationResult = otpService.verifyOTP(phone, otpCode);

            if (!verificationResult.isSuccess() || !verificationResult.getData()) {
                return ApiResponse.error("Code OTP invalide ou expiré");
            }

            User user = userRepository.findByPhone(phone)
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

            // Mise à jour du profil completion
            updateProfileCompletion(user);
            userRepository.save(user);

            String jwtToken = jwtService.generateToken(user);
            LoginResponse response = createAuthResponse(user, jwtToken);

            log.info("Connexion OTP réussie pour: {}", user.getUsername());
            return ApiResponse.success(response, "Connexion OTP réussie");

        } catch (Exception e) {
            log.error("Échec connexion OTP", e);
            return ApiResponse.error("Échec connexion OTP: " + e.getMessage());
        }
    }

    public ApiResponse<String> verifyEmail(String token) {
        try {
            Optional<User> userOpt = userRepository.findByEmailVerificationToken(token);

            if (userOpt.isEmpty()) {
                return ApiResponse.error("Token de vérification invalide");
            }

            User user = userOpt.get();

            if (user.getEmailVerificationExpiry().isBefore(LocalDateTime.now())) {
                return ApiResponse.error("Token de vérification expiré");
            }

            // Marquer l'email comme vérifié
            user.setEmailVerified(true);
            user.setEmailVerificationToken(null);
            user.setEmailVerificationExpiry(null);

            // Mise à jour du profil completion
            updateProfileCompletion(user);
            userRepository.save(user);

            // Notification de confirmation
            notificationService.createNotification(
                    user.getId(),
                    "Email vérifié",
                    "Votre adresse email a été vérifiée avec succès !",
                    "EMAIL"
            );

            log.info("Email vérifié pour: {}", user.getEmail());
            return ApiResponse.success("Email vérifié avec succès");

        } catch (Exception e) {
            log.error("Échec vérification email", e);
            return ApiResponse.error("Échec vérification email: " + e.getMessage());
        }
    }

    public ApiResponse<String> resendVerificationEmail(String email) {
        try {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

            if (user.isEmailVerified()) {
                return ApiResponse.error("Email déjà vérifié");
            }

            // Générer nouveau token
            user.setEmailVerificationToken(generateEmailVerificationToken());
            user.setEmailVerificationExpiry(LocalDateTime.now().plusHours(emailVerificationExpirationHours));
            userRepository.save(user);

            // Renvoyer l'email
            emailService.sendVerificationEmail(user.getEmail(), user.getEmailVerificationToken());

            return ApiResponse.success("Email de vérification renvoyé");

        } catch (Exception e) {
            log.error("Échec renvoi email vérification", e);
            return ApiResponse.error("Échec renvoi email: " + e.getMessage());
        }
    }

    // Méthode pour obtenir les prochaines étapes utilisateur
    public ApiResponse<List<String>> getNextSteps(Long userId) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

            List<String> steps = calculateNextSteps(user);
            return ApiResponse.success(steps, "Prochaines étapes récupérées");

        } catch (Exception e) {
            log.error("Erreur récupération prochaines étapes", e);
            return ApiResponse.error("Erreur: " + e.getMessage());
        }
    }

    // ============================================
    // MÉTHODES PRIVÉES
    // ============================================

    private List<String> calculateNextSteps(User user) {
        List<String> steps = new ArrayList<>();

        if (!user.isEmailVerified()) {
            steps.add("Vérification de votre adresse e-mail");
        }

        if (!user.isPhoneVerified()) {
            steps.add("Vérification de votre numéro de téléphone");
        }

        if (user.getEkyc() == null) {
            steps.add("Informations sur votre entreprise");
        } else {
            switch (user.getEkyc().getStatus()) {
                case DRAFT:
                    if (user.getEkyc().getDocuments().isEmpty()) {
                        steps.add("Téléchargement de documents");
                    } else {
                        steps.add("Soumission de votre dossier eKYC");
                    }
                    break;
                case PENDING:
                    steps.add("En attente de vérification");
                    break;
                case UNDER_REVIEW:
                    steps.add("Dossier en cours d'examen");
                    break;
                case APPROVED:
                    steps.add("Dossier approuvé - Accès aux services");
                    break;
                case REJECTED:
                    steps.add("Dossier rejeté - Contactez le support");
                    break;
            }
        }

        return steps;
    }

    private Double calculateInitialProfileCompletion(User user) {
        double completion = 0.0;

        // Informations de base (40% max)
        if (user.getFirstName() != null && !user.getFirstName().trim().isEmpty()) completion += 10;
        if (user.getLastName() != null && !user.getLastName().trim().isEmpty()) completion += 10;
        if (user.getEmail() != null && !user.getEmail().trim().isEmpty()) completion += 10;
        if (user.getPhone() != null && !user.getPhone().trim().isEmpty()) completion += 10;

        return completion;
    }

    private void updateProfileCompletion(User user) {
        double completion = 0.0;

        // Informations de base (40%)
        if (user.getFirstName() != null && !user.getFirstName().trim().isEmpty()) completion += 10;
        if (user.getLastName() != null && !user.getLastName().trim().isEmpty()) completion += 10;
        if (user.getEmail() != null && !user.getEmail().trim().isEmpty()) completion += 10;
        if (user.getPhone() != null && !user.getPhone().trim().isEmpty()) completion += 10;

        // Statut de vérification (30%)
        if (user.isEmailVerified()) completion += 15;
        if (user.isPhoneVerified()) completion += 15;

        // Completion eKYC (30%)
        if (user.getEkyc() != null) {
            completion += 15; // eKYC de base soumis

            // Documents uploadés
            if (!user.getEkyc().getDocuments().isEmpty()) {
                completion += 10; // Documents uploadés
            }

            // eKYC approuvé
            if (user.getEkyc().getStatus() == ApplicationStatus.APPROVED) {
                completion += 5; // Bonus pour approbation
            }
        }

        user.setProfileCompletion(Math.min(100.0, completion));
        user.setVerified(completion >= 90.0);
    }

    public LoginResponse createAuthResponse(User user, String token) {
        return new LoginResponse(
                token,
                jwtService.getExpirationTime(),
                user.getRole().name(),
                user.getId(),
                user.getFullName(),
                user.isVerified(),
                user.isPhoneVerified(),
                user.isEmailVerified(),
                user.getProfileCompletion(),
                calculateNextSteps(user)
        );
    }

    private String generateEmailVerificationToken() {
        return UUID.randomUUID().toString();
    }
}
