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
    @NotNull(message = "Le type de document est obligatoire")
    private DocumentType type;

    @NotBlank(message = "Le nom du document est obligatoire")
    private String name;

    private String description;

    @NotEmpty(message = "Au moins un fichier est requis")
    private List<MultipartFile> files;

    private List<Media> documents;
}