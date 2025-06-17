package com.talaty.controller;

import com.talaty.dto.ApiResponse;
import com.talaty.dto.request.DocumentUploadDto;
import com.talaty.dto.response.DocumentResponseDto;
import com.talaty.enums.DocumentType;
import com.talaty.model.User;
import com.talaty.service.DocumentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
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

    @PostMapping("/upload")
    public ResponseEntity<ApiResponse<DocumentResponseDto>> uploadDocument(
            Authentication authentication,
            @RequestParam("ekycId") Long ekycId,
            @RequestParam("documentType") DocumentType documentType,
            @RequestParam("documentName") String documentName,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "required", defaultValue = "false") boolean required,
            @RequestParam("files") List<MultipartFile> files) {

        Long userId = ((User) authentication.getPrincipal()).getId();

        DocumentUploadDto request = new DocumentUploadDto(ekycId, documentType, documentName, description, required);
        ApiResponse<DocumentResponseDto> response = documentService.uploadDocument(userId, request, files);

        return response.isSuccess()
                ? ResponseEntity.ok(response)
                : ResponseEntity.badRequest().body(response);
    }

    @GetMapping("/ekyc/{ekycId}")
    public ResponseEntity<ApiResponse<List<DocumentResponseDto>>> getDocumentsByEKYC(
            Authentication authentication,
            @PathVariable Long ekycId) {

        Long userId = ((User) authentication.getPrincipal()).getId();
        ApiResponse<List<DocumentResponseDto>> response = documentService.getDocumentsByEKYC(userId, ekycId);

        return response.isSuccess()
                ? ResponseEntity.ok(response)
                : ResponseEntity.badRequest().body(response);
    }
}

