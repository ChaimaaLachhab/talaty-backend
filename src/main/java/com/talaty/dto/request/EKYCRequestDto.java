package com.talaty.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EKYCRequestDto {
    private String nationalId;

    @NotBlank(message = "Le nom de l'entreprise est requis")
    private String companyName;

    @NotBlank(message = "Le secteur d'activité est requis")
    private String businessSector;

    @NotBlank(message = "L'objet de l'entreprise est requis")
    private String businessPurpose;

    private String address;
    private String city;
    private String country = "Morocco";
    private String postalCode;

    private String companyRegistrationNumber;
    private LocalDate companyEstablishedDate;

    @Size(max = 34, message = "Le numéro de compte bancaire ne doit pas dépasser 34 caractères")
    private String bankAccountNumber;

    private String bankName;

    @DecimalMin(value = "0.0", message = "Le revenu mensuel doit être positif")
    private Double monthlyRevenue;

    @DecimalMin(value = "0.0", message = "Le revenu annuel doit être positif")
    private Double annualRevenue;

    @Min(value = 1, message = "Le nombre d'employés doit être d'au moins 1")
    private Integer numberOfEmployees;

    @DecimalMin(value = "0.0", message = "Le montant de crédit demandé doit être positif")
    private Double requestedCreditAmount;

    private String creditPurpose;
}