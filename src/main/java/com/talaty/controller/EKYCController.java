package com.talaty.controller;

import com.talaty.dto.ApiResponse;
import com.talaty.dto.request.EKYCRequestDto;
import com.talaty.dto.response.EKYCResponseDto;
import com.talaty.model.User;
import com.talaty.service.EKYCService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ekyc")
@PreAuthorize("hasRole('USER')")
public class EKYCController {

    private final EKYCService ekycService;

    public EKYCController(EKYCService ekycService) {
        this.ekycService = ekycService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<EKYCResponseDto>> createOrUpdateEKYC(
            Authentication authentication,
            @Valid @RequestBody EKYCRequestDto request) {

        Long userId = ((User) authentication.getPrincipal()).getId();
        ApiResponse<EKYCResponseDto> response = ekycService.createOrUpdateEKYC(userId, request);

        return response.isSuccess()
                ? ResponseEntity.ok(response)
                : ResponseEntity.badRequest().body(response);
    }

    @PostMapping("/{ekycId}/submit")
    public ResponseEntity<ApiResponse<EKYCResponseDto>> submitEKYC(
            Authentication authentication,
            @PathVariable Long ekycId) {

        Long userId = ((User) authentication.getPrincipal()).getId();
        ApiResponse<EKYCResponseDto> response = ekycService.submitEKYC(userId, ekycId);

        return response.isSuccess()
                ? ResponseEntity.ok(response)
                : ResponseEntity.badRequest().body(response);
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<EKYCResponseDto>> getMyEKYC(Authentication authentication) {
        Long userId = ((User) authentication.getPrincipal()).getId();
        ApiResponse<EKYCResponseDto> response = ekycService.getMyEKYC(userId);

        return response.isSuccess()
                ? ResponseEntity.ok(response)
                : ResponseEntity.badRequest().body(response);
    }
}
