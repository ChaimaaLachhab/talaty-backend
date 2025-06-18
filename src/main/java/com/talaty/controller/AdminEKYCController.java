package com.talaty.controller;

import com.talaty.dto.ApiResponse;
import com.talaty.dto.request.AdminReviewDto;
import com.talaty.dto.response.EKYCResponseDto;
import com.talaty.enums.ApplicationStatus;
import com.talaty.model.User;
import com.talaty.service.AdminEKYCService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/ekyc")
@PreAuthorize("hasRole('ADMIN')")
public class AdminEKYCController {

    private final AdminEKYCService adminEKYCService;

    public AdminEKYCController(AdminEKYCService adminEKYCService) {
        this.adminEKYCService = adminEKYCService;
    }

    @GetMapping("/pending")
    public ResponseEntity<ApiResponse<List<EKYCResponseDto>>> getPendingEKYCs() {
        ApiResponse<List<EKYCResponseDto>> response = adminEKYCService.getPendingEKYCs();

        return response.isSuccess()
                ? ResponseEntity.ok(response)
                : ResponseEntity.badRequest().body(response);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<EKYCResponseDto>>> getEKYCsByStatus(@PathVariable ApplicationStatus status) {
        ApiResponse<List<EKYCResponseDto>> response = adminEKYCService.getEKYCsByStatus(status);

        return response.isSuccess()
                ? ResponseEntity.ok(response)
                : ResponseEntity.badRequest().body(response);
    }

    @PutMapping("/{ekycId}/review")
    public ResponseEntity<ApiResponse<EKYCResponseDto>> reviewEKYC(
            @AuthenticationPrincipal User user,
            @PathVariable Long ekycId,
            @Valid @RequestBody AdminReviewDto reviewDto) {

        ApiResponse<EKYCResponseDto> response = adminEKYCService.reviewEKYC(user.getId(), ekycId, reviewDto);

        return response.isSuccess()
                ? ResponseEntity.ok(response)
                : ResponseEntity.badRequest().body(response);
    }

    @PutMapping("/document/{documentId}/verify")
    public ResponseEntity<ApiResponse<String>> verifyDocument(
            @AuthenticationPrincipal User user,
            @PathVariable Long documentId,
            @RequestParam boolean verified,
            @RequestParam(required = false) String notes) {

        ApiResponse<String> response = adminEKYCService.verifyDocument(user.getId(), documentId, verified, notes);

        return response.isSuccess()
                ? ResponseEntity.ok(response)
                : ResponseEntity.badRequest().body(response);
    }
}
