package com.talaty.controller;

import com.talaty.dto.ApiResponse;
import com.talaty.dto.request.AdminRequestDto;
import com.talaty.dto.request.DocumentUploadDto;
import com.talaty.dto.response.AdminResponseDto;
import com.talaty.dto.response.DocumentResponseDto;
import com.talaty.enums.DocumentType;
import com.talaty.model.User;
import com.talaty.service.DocumentService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/documents")
@PreAuthorize("hasRole('USER')")
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @PostMapping(value ="/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<DocumentResponseDto>> uploadDocument(
            @AuthenticationPrincipal User user,
            @RequestPart("documentInfo") DocumentUploadDto request,
            @RequestPart("files") List<MultipartFile> files) {

        ApiResponse<DocumentResponseDto> response = documentService.uploadDocument(user.getId(), request, files);

        return response.isSuccess()
                ? ResponseEntity.ok(response)
                : ResponseEntity.badRequest().body(response);
    }

    @GetMapping("/ekyc/{ekycId}")
    public ResponseEntity<ApiResponse<List<DocumentResponseDto>>> getDocumentsByEKYC(
            @AuthenticationPrincipal User user,
            @PathVariable Long ekycId) {

        ApiResponse<List<DocumentResponseDto>> response = documentService.getDocumentsByEKYC(user.getId(), ekycId);

        return response.isSuccess()
                ? ResponseEntity.ok(response)
                : ResponseEntity.badRequest().body(response);
    }
}

