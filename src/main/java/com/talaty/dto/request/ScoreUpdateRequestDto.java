//package com.talaty.dto.request;
//
//import com.talaty.enums.ApplicationStatus;
//import jakarta.validation.constraints.*;
//import lombok.*;
//
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//public class ScoreUpdateRequestDto {
//    @NotNull(message = "Score is required")
//    @Min(value = 0, message = "Score must be non-negative")
//    @Max(value = 100, message = "Score cannot exceed 100")
//    private Integer score;
//
//    private String reviewComments;
//
//    @NotNull(message = "Status is required")
//    private ApplicationStatus status;
//}
