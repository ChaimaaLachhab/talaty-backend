package com.talaty.dto.response;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileCompletionDto {
    private Double completionPercentage;
    private List<String> completedSteps;
    private List<String> pendingSteps;
    private String nextStep;
    private boolean canApplyForCredit;
}
