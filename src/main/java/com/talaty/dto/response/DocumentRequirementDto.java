package com.talaty.dto.response;

import com.talaty.enums.DocumentType;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentRequirementDto {
    private DocumentType type;
    private String name;
    private String description;
    private boolean required;
    private boolean submitted;
    private boolean verified;
    private List<String> acceptedFormats;
    private Long maxFileSizeMB;
    private Integer minFiles;
    private Integer maxFiles;
}