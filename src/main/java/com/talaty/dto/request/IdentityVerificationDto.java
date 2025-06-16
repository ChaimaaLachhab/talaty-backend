package com.talaty.dto.request;

import com.talaty.enums.DocumentType;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IdentityVerificationDto {
    @NotNull(message = "Le type de document d'identité est obligatoire")
    private DocumentType idType; // CIN, PASSPORT, DRIVING_LICENSE

    @NotNull(message = "La photo du document est obligatoire")
    private MultipartFile idDocument;

    @NotNull(message = "Le selfie est obligatoire")
    private MultipartFile selfiePhoto;
}
