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
    private boolean dataExtracted;
    private String processingNotes;
    private LocalDateTime processedAt;
    private List<MediaResponseDto> mediaFiles;
    private ExtractedDataDto extractedData;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}