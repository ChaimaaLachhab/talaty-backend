package com.talaty.controller;

import com.talaty.dto.*;
import com.talaty.dto.request.*;
import com.talaty.dto.response.AdminResponseDto;
import com.talaty.dto.response.CustomerResponseDto;
import com.talaty.dto.response.OTPResponseDto;
import com.talaty.mapper.UserMapper;
import com.talaty.model.Admin;
import com.talaty.model.Customer;
import com.talaty.model.User;
import com.talaty.service.AuthenticationService;
import com.talaty.service.OTPService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final OTPService otpService;
    private final UserMapper userMapper;

    @Autowired
    public AuthenticationController(AuthenticationService authenticationService,
                                    OTPService otpService,
                                    UserMapper userMapper) {
        this.authenticationService = authenticationService;
        this.otpService = otpService;
        this.userMapper = userMapper;
    }

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<CustomerResponseDto>> register(@Valid @RequestBody CustomerRequestDto customerDTO) {
        ApiResponse<CustomerResponseDto> response = authenticationService.signup(customerDTO);

        return response.isSuccess()
                ? ResponseEntity.ok(response)
                : ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/add-admin")
    public ResponseEntity<ApiResponse<AdminResponseDto>> addAdmin(@Valid @RequestBody AdminRequestDto adminDTO) {
        ApiResponse<AdminResponseDto> response = authenticationService.addAdmin(adminDTO);

        return response.isSuccess()
                ? ResponseEntity.ok(response)
                : ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> authenticate(@Valid @RequestBody LoginRequest loginRequest) {
        ApiResponse<LoginResponse> response = authenticationService.authenticate(loginRequest);

        return response.isSuccess()
                ? ResponseEntity.ok(response)
                : ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @PostMapping("/login/otp")
    public ResponseEntity<ApiResponse<LoginResponse>> authenticateWithOTP(@Valid @RequestBody OTPVerifyDto otpRequest) {
        ApiResponse<LoginResponse> response = authenticationService.authenticateWithOTP(
                otpRequest.getPhone(),
                otpRequest.getOtpCode()
        );

        return response.isSuccess()
                ? ResponseEntity.ok(response)
                : ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @GetMapping("/verify-email")
    public ResponseEntity<ApiResponse<String>> verifyEmail(@RequestParam String token) {
        ApiResponse<String> response = authenticationService.verifyEmail(token);

        return response.isSuccess()
                ? ResponseEntity.ok(response)
                : ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @PostMapping("/resend-verification")
    public ResponseEntity<ApiResponse<String>> resendVerificationEmail(@RequestParam String email) {
        ApiResponse<String> response = authenticationService.resendVerificationEmail(email);

        return response.isSuccess()
                ? ResponseEntity.ok(response)
                : ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @GetMapping("/next-steps")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<String>>> getNextSteps(Authentication authentication) {
        Long userId = ((User) authentication.getPrincipal()).getId();
        ApiResponse<List<String>> response = authenticationService.getNextSteps(userId);

        return response.isSuccess()
                ? ResponseEntity.ok(response)
                : ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // Endpoints OTP intégrés dans AuthController pour cohérence
    @PostMapping("/otp/send")
    public ResponseEntity<ApiResponse<OTPResponseDto>> sendOTP(@Valid @RequestBody OTPRequestDto request) {
        ApiResponse<OTPResponseDto> response = otpService.generateAndSendOTP(request.getPhone());

        return response.isSuccess()
                ? ResponseEntity.ok(response)
                : ResponseEntity.badRequest().body(response);
    }

    @PostMapping("/otp/verify")
    public ResponseEntity<ApiResponse<LoginResponse>> verifyOTP(@Valid @RequestBody OTPVerifyDto request) {
        ApiResponse<LoginResponse> response = authenticationService.authenticateWithOTP(
                request.getPhone(),
                request.getOtpCode()
        );

        return response.isSuccess()
                ? ResponseEntity.ok(response)
                : ResponseEntity.badRequest().body(response);
    }
}