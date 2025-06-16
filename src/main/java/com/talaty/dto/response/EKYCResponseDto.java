package com.talaty.dto.response;

import com.talaty.enums.ApplicationStatus;
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
public class EKYCResponseDto {
    private Long id;
    private String nationalId;

    // Business Information
    private String companyName;
    private String businessSector;
    private String businessPurpose;
    private String address;
    private String city;
    private String country;
    private String postalCode;

    // Registration Information
    private String companyRegistrationNumber;
    private LocalDateTime companyEstablishedDate;

    // Banking Information
    private String bankAccountNumber;
    private String bankName;

    // Financial Information
    private Double monthlyRevenue;
    private Double annualRevenue;
    private Integer numberOfEmployees;

    // Credit Application
    private Double requestedCreditAmount;
    private String creditPurpose;

    // Status and Scoring
    private ApplicationStatus status;
    private Integer score;
    private Integer maxScore;
    private Double scorePercentage;

    // Documents
    private List<DocumentResponseDto> documents;

    // Processing Information
    private String reviewComments;
    private LocalDateTime submittedAt;
    private LocalDateTime reviewedAt;
    private Long reviewedBy;

    // Progress tracking
    private boolean nationalIdVerified;
    private boolean companyDocumentsSubmitted;
    private boolean bankStatementsSubmitted;
    private boolean identityVerified;
    private boolean phoneVerified;
    private boolean emailVerified;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}