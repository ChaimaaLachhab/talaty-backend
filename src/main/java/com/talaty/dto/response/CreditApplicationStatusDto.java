package com.talaty.dto.response;

import com.talaty.enums.ApplicationStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreditApplicationStatusDto {
    private Long applicationId;
    private ApplicationStatus status;
    private Integer currentScore;
    private Integer maxScore;
    private Double scorePercentage;
    private String statusMessage;
    private List<String> requiredActions;
    private LocalDateTime lastUpdated;
    private Double requestedAmount;
    private boolean canResubmit;
}
