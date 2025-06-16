package com.talaty.dto.response;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OnboardingStepDto {
    private String stepId;
    private String title;
    private String description;
    private boolean completed;
    private boolean current;
    private int order;
    private List<String> requiredActions;
}
