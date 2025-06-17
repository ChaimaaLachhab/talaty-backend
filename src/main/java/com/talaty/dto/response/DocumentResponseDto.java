package com.talaty.dto.response;

import com.talaty.enums.DocumentType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DocumentResponseDto {
    private Long id;
    private DocumentType type;
    private String name;
    private String description;
    private boolean required;
    private boolean verified;
    private String processingNotes;
    private LocalDateTime processedAt;
    private Long processedBy;
    private String extractedData;
    private boolean dataExtracted;
    private LocalDateTime dataExtractedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<MediaResponseDto> mediaFiles;
}