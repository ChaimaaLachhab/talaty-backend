package com.talaty.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EKYCUpdateDto {
    // Personal Information from ID scan
    private String nationalId;
    private String firstName; // Extracted from ID
    private String lastName;  // Extracted from ID
    private LocalDate dateOfBirth; // Extracted from ID
    private String birthPlace; // Extracted from ID

    // Business Information
    @NotBlank(message = "Le nom de l'entreprise est obligatoire")
    private String companyName;

    @NotBlank(message = "Le secteur d'activité est obligatoire")
    private String businessSector;

    @NotBlank(message = "La raison sociale est obligatoire")
    private String businessPurpose;

    private String address;
    private String city;
    private String postalCode;

    // Registration Information
    private String companyRegistrationNumber;
    private LocalDateTime companyEstablishedDate;

    // Banking Information (RIB format)
    @Pattern(regexp = "^[0-9]{3}-[0-9]{3}-[0-9]{16}-[0-9]{2}$",
            message = "Format RIB invalide (000-000-0000000000000000-00)")
    private String bankAccountNumber;

    private String bankName;

    // Financial Information
    @Positive(message = "Le chiffre d'affaires mensuel doit être positif")
    private Double monthlyRevenue;

    @Positive(message = "Le chiffre d'affaires annuel doit être positif")
    private Double annualRevenue;

    @Positive(message = "Le nombre d'employés doit être positif")
    private Integer numberOfEmployees;

    // Credit Application
    @Positive(message = "Le montant de crédit demandé doit être positif")
    private Double requestedCreditAmount;

    private String creditPurpose;
}