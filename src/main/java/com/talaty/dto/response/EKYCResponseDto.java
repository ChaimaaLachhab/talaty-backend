package com.talaty.dto.response;

import com.talaty.enums.ApplicationStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EKYCResponseDto {
    private Long id;
    private String nationalId;
    private String companyName;
    private String businessSector;
    private String businessPurpose;
    private String address;
    private String city;
    private String country;
    private String postalCode;
    private String companyRegistrationNumber;
    private LocalDate companyEstablishedDate;
    private String bankAccountNumber;
    private String bankName;
    private Double monthlyRevenue;
    private Double annualRevenue;
    private Integer numberOfEmployees;
    private Double requestedCreditAmount;
    private String creditPurpose;
    private ApplicationStatus status;
    private Integer score;
    private Integer maxScore;
    private Double scorePercentage;
    private String reviewComments;
    private LocalDateTime submittedAt;
    private LocalDateTime reviewedAt;
    private Long reviewedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<DocumentResponseDto> documents;
}