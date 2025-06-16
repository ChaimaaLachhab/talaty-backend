package com.talaty.dto.response;

import com.talaty.enums.DocumentType;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExtractedDataDto {
    private DocumentType documentType;
    private Map<String, Object> extractedFields;
    private Double confidenceScore;
    private LocalDateTime extractedAt;
    private String extractionMethod; // OCR, API, MANUAL
    private List<String> extractionErrors;
}
