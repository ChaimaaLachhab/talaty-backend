package com.talaty.dto.request;

import com.talaty.enums.DocumentType;
import com.talaty.model.Media;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DocumentUploadDto {
    @NotNull(message = "L'ID de l'eKYC est requis")
    private Long ekycId;

    @NotNull(message = "Le type de document est requis")
    private DocumentType documentType;

    @NotBlank(message = "Le nom du document est requis")
    private String documentName;

    private String description;
    private boolean required = false;
}