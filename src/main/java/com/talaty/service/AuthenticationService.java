package com.talaty.service;

import com.talaty.dto.ApiResponse;
import com.talaty.dto.LoginResponse;
import com.talaty.dto.LoginRequest;
import com.talaty.dto.request.AdminRequestDto;
import com.talaty.dto.request.CustomerRequestDto;
import com.talaty.dto.request.OTPRequest;
import com.talaty.enums.ApplicationStatus;
import com.talaty.mapper.UserMapper;
import com.talaty.model.Admin;
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

    @Value("${email.verification.expiration-hours}")
    private int emailVerificationExpirationHours;

    @Value("${otp.expiration-minutes}")
    private int otpExpirationMinutes;

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final EmailService emailService;
    private final NotificationService notificationService;

    @Autowired
    public AuthenticationService(UserRepository userRepository, UserMapper userMapper, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtService jwtService, EmailService emailService, NotificationService notificationService) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.emailService = emailService;
        this.notificationService = notificationService;
    }

    public ApiResponse<User> signup(CustomerRequestDto request) {
        try {
            if (userRepository.findByUsername(request.getUsername()).isPresent()) {
                return ApiResponse.error("Username already exists");
            }
            if (userRepository.findByEmail(request.getEmail()).isPresent()) {
                return ApiResponse.error("Email already exists");
            }

            if (request.getPhone() != null && userRepository.existsByPhone(request.getPhone())) {
                return ApiResponse.error("Phone number already exists");
            }

            User user = userMapper.toCustomerEntity(request);
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setEmailVerificationToken(generateEmailVerificationToken());
            user.setEmailVerificationExpiry(LocalDateTime.now().plusHours(emailVerificationExpirationHours));

            // Set initial profile completion based on provided data
            user.setProfileCompletion(calculateInitialProfileCompletion(user));

            userRepository.save(user);

            // Send verification email
            emailService.sendVerificationEmail(user.getEmail(), user.getEmailVerificationToken());

            // Create notification
            notificationService.createNotification(
                    user.getId(),
                    "Welcome to Talaty!",
                    "Please verify your email to complete registration.",
                    "EMAIL"
            );

            return ApiResponse.success("Registration successful. Please check your email for verification.", user);

        } catch (Exception e) {
            log.error("Registration failed", e);
            return ApiResponse.error("Registration failed: " + e.getMessage());
        }
    }

    public Admin addAdmin(AdminRequestDto input) {
        if (userRepository.findByUsername(input.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.findByEmail(input.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        Admin admin = userMapper.toAdminEntity(input);
        admin.setPassword(passwordEncoder.encode(input.getPassword()));
        admin.setProfileCompletion(100.0);
        return userRepository.save(admin);
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

            updateProfileCompletion(user);

            String token = jwtService.generateToken(user);
            LoginResponse response = createAuthResponse(user, token);

            return ApiResponse.success("Login successful", response);

        } catch (BadCredentialsException e) {
            log.error("Login failed", e);
            return ApiResponse.error("Bad Credentials");
        } catch (Exception e) {
            log.error("Connexion failed", e);
            return ApiResponse.error("Connexion failed: " + e.getMessage());
        }

    }

    public ApiResponse<String> generateOTP(String username) {
        try {
            Optional<User> userOpt = userRepository.findByUsernameOrEmail(username);

            if (userOpt.isEmpty()) {
                return ApiResponse.error("User not found");
            }

            User user = userOpt.get();
            String otpCode = generateOTPCode();

            user.setOtpCode(otpCode);
            user.setOtpExpiryTime(LocalDateTime.now().plusMinutes(otpExpirationMinutes));
            userRepository.save(user);

            // Send OTP via email
            emailService.sendOTPEmail(user.getEmail(), otpCode);

            return ApiResponse.success("OTP sent successfully");

        } catch (Exception e) {
            log.error("OTP generation failed", e);
            return ApiResponse.error("Failed to generate OTP: " + e.getMessage());
        }
    }

    public ApiResponse<LoginResponse> verifyOTP(OTPRequest request) {
        try {
            Optional<User> userOpt = userRepository.findByUsernameOrEmail(request.getUsername());

            if (userOpt.isEmpty()) {
                return ApiResponse.error("User not found");
            }

            User user = userOpt.get();

            if (user.getOtpCode() == null || user.getOtpExpiryTime() == null) {
                return ApiResponse.error("No OTP generated for this user");
            }

            if (LocalDateTime.now().isAfter(user.getOtpExpiryTime())) {
                return ApiResponse.error("OTP has expired");
            }

            if (!user.getOtpCode().equals(request.getOtpCode())) {
                return ApiResponse.error("Invalid OTP");
            }

            // Clear OTP and mark phone as verified
            user.setOtpCode(null);
            user.setOtpExpiryTime(null);
            user.setPhoneVerified(true);

            // Update profile completion
            updateProfileCompletion(user);
            userRepository.save(user);

            String token = jwtService.generateToken(user);
            LoginResponse response = createAuthResponse(user, token);

            return ApiResponse.success("OTP verification successful", response);

        } catch (Exception e) {
            log.error("OTP verification failed", e);
            return ApiResponse.error("OTP verification failed: " + e.getMessage());
        }
    }

    public ApiResponse<String> verifyEmail(String token) {
        try {
            Optional<User> userOpt = userRepository.findByEmailVerificationToken(token);

            if (userOpt.isEmpty()) {
                return ApiResponse.error("Invalid verification token");
            }

            User user = userOpt.get();

            if (user.getEmailVerificationExpiry().isBefore(LocalDateTime.now())) {
                return ApiResponse.error("Verification token has expired");
            }

            user.setVerified(true);
            user.setEmailVerificationToken(null);
            user.setEmailVerificationExpiry(null);

            // Update profile completion
            updateProfileCompletion(user);
            userRepository.save(user);

            notificationService.createNotification(
                    user.getId(),
                    "Email Verified",
                    "Your email has been successfully verified!",
                    "EMAIL"
            );

            return ApiResponse.success("Email verified successfully");

        } catch (Exception e) {
            log.error("Email verification failed", e);
            return ApiResponse.error("Email verification failed: " + e.getMessage());
        }
    }

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
        } else if (user.getEkyc().getDocuments().isEmpty()) {
            steps.add("Téléchargement de documents");
        } else if (user.getEkyc().getStatus() == ApplicationStatus.PENDING) {
            steps.add("En attente de vérification");
        }

        return steps;
    }

    private Double calculateInitialProfileCompletion(User user) {
        double completion = 0.0;

        // Basic info (40% max)
        if (user.getFirstName() != null && !user.getFirstName().trim().isEmpty()) completion += 10;
        if (user.getLastName() != null && !user.getLastName().trim().isEmpty()) completion += 10;
        if (user.getEmail() != null && !user.getEmail().trim().isEmpty()) completion += 10;
        if (user.getPhone() != null && !user.getPhone().trim().isEmpty()) completion += 10;

        return completion;
    }

    private void updateProfileCompletion(User user) {
        double completion = 0.0;

        // Basic information (40%)
        if (user.getFirstName() != null && !user.getFirstName().trim().isEmpty()) completion += 10;
        if (user.getLastName() != null && !user.getLastName().trim().isEmpty()) completion += 10;
        if (user.getEmail() != null && !user.getEmail().trim().isEmpty()) completion += 10;
        if (user.getPhone() != null && !user.getPhone().trim().isEmpty()) completion += 10;

        // Verification status (30%)
        if (user.isEmailVerified()) completion += 15;
        if (user.isPhoneVerified()) completion += 15;

        // eKYC completion (30%)
        if (user.getEkyc() != null) {
            completion += 15; // Basic eKYC submitted
            if (!user.getEkyc().getDocuments().isEmpty()) {
                completion += 15; // Documents uploaded
            }
        }

        user.setProfileCompletion(Math.min(100.0, completion));
        user.setVerified(completion >= 100.0);
    }

    private LoginResponse createAuthResponse(User user, String token) {
        return new LoginResponse(
                token,
                jwtService.getExpirationTime(),
                user.getRole().name(),
                user.getId(),
                user.getFullName(),
                user.isVerified(),
                user.isPhoneVerified(),
                user.isEmailVerified(),
                user.getProfileCompletion()
        );
    }

    private String generateEmailVerificationToken() {
        return UUID.randomUUID().toString();
    }

    private String generateOTPCode() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(999999));
    }
}
