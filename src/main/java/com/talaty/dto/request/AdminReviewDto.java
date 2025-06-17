package com.talaty.dto.request;

import com.talaty.enums.ApplicationStatus;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminReviewDto {
    @NotNull(message = "La décision est requise")
    private ApplicationStatus decision;

    private String comments;

    @Min(value = 0, message = "Le score doit être positif")
    @Max(value = 100, message = "Le score ne peut pas dépasser 100")
    private Integer finalScore;
}
