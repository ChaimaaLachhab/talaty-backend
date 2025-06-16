package com.talaty.controller;

import com.talaty.dto.*;
import com.talaty.dto.request.AdminRequestDto;
import com.talaty.dto.request.CustomerRequestDto;
import com.talaty.dto.request.OTPRequest;
import com.talaty.dto.response.AdminResponseDto;
import com.talaty.dto.response.CustomerResponseDto;
import com.talaty.mapper.UserMapper;
import com.talaty.model.Admin;
import com.talaty.model.Customer;
import com.talaty.model.User;
import com.talaty.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final UserMapper userMapper;

    @Autowired
    public AuthenticationController(AuthenticationService authenticationService, UserMapper userMapper) {
        this.authenticationService = authenticationService;
        this.userMapper = userMapper;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> register(@RequestBody CustomerRequestDto customerDTO) {
        ApiResponse<User> response = authenticationService.signup(customerDTO);

        if (response.isSuccess()) {
            CustomerResponseDto responseDto = userMapper.toCustomerResponseDto((Customer) response.getData());
            return ResponseEntity.ok(ApiResponse.success(response.getMessage(), responseDto));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/add-admin")
    public ResponseEntity<?> addAdmin(@RequestBody AdminRequestDto adminDTO) {
        try {
            Admin newAdmin = authenticationService.addAdmin(adminDTO);
            AdminResponseDto responseDto = userMapper.toAdminResponseDto(newAdmin);
            return ResponseEntity.ok(ApiResponse.success("Admin created successfully", responseDto));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("An error occurred while creating admin"));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> authenticate(@RequestBody LoginRequest loginRequest) {
        ApiResponse<LoginResponse> response = authenticationService.authenticate(loginRequest);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    @PostMapping("/generate-otp")
    public ResponseEntity<ApiResponse<String>> generateOTP(@RequestBody String username) {
        ApiResponse<String> response = authenticationService.generateOTP(username);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse<LoginResponse>> verifyOTP(@RequestBody OTPRequest otpRequest) {
        ApiResponse<LoginResponse> response = authenticationService.verifyOTP(otpRequest);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @GetMapping("/verify-email")
    public ResponseEntity<ApiResponse<String>> verifyEmail(@RequestParam String token) {
        ApiResponse<String> response = authenticationService.verifyEmail(token);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}