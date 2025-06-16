package com.talaty.dto.response;

import com.talaty.enums.RiskLevel;
import lombok.*;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScoreCalculationDto {
    private Integer totalScore;
    private Integer maxScore;
    private Double scorePercentage;
    private Map<String, Integer> categoryScores;
    private List<String> strengths;
    private List<String> weaknesses;
    private List<String> recommendations;
    private RiskLevel riskLevel;
    private boolean approvalRecommended;
}
