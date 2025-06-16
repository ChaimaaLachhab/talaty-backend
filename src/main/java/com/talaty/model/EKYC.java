package com.talaty.model;

import com.talaty.enums.DocumentType;
import com.talaty.enums.ApplicationStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class EKYC {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String nationalId;

    // Business Information
    @Column(nullable = false)
    private String companyName;

    @Column(nullable = false)
    private String businessSector;

    @Column(nullable = false)
    private String businessPurpose;

    private String address;
    private String city;
    private String country = "Morocco";
    private String postalCode;

    // Registration Information
    private String companyRegistrationNumber;
    private LocalDateTime companyEstablishedDate;

    // Banking Information
    @Column(length = 34) // IBAN format
    private String bankAccountNumber;

    private String bankName;

    // Financial Information
    private Double monthlyRevenue;
    private Double annualRevenue;
    private Integer numberOfEmployees;


    // Credit Application
    private Double requestedCreditAmount;
    private String creditPurpose;

    @OneToMany(mappedBy = "ekyc", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Document> documents = new ArrayList<>();

    // Scoring and Status
    @Enumerated(EnumType.STRING)
    private ApplicationStatus status = ApplicationStatus.PENDING;

    private Integer score = 0;
    private Integer maxScore = 100;

    // Processing Information
    @Column(length = 1000)
    private String reviewComments;

    private LocalDateTime submittedAt;
    private LocalDateTime reviewedAt;
    private Long reviewedBy; // ID de l'admin qui a reviewé

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // Méthodes utilitaires
    public void addDocument(Document document) {
        documents.add(document);
        document.setEkyc(this);
    }

    public void removeDocument(Document document) {
        documents.remove(document);
        document.setEkyc(null);
    }

    public Double getScorePercentage() {
        return maxScore > 0 ? (score.doubleValue() / maxScore.doubleValue()) * 100 : 0.0;
    }

    public boolean isDocumentSubmitted(DocumentType type) {
        return documents.stream()
                .anyMatch(doc -> doc.getType() == type && !doc.getMediaFiles().isEmpty());
    }

    public boolean areRequiredDocumentsSubmitted() {
        return isDocumentSubmitted(DocumentType.NATIONAL_ID) &&
                isDocumentSubmitted(DocumentType.COMPANY_REGISTRATION) &&
                documents.stream()
                        .filter(doc -> doc.getType() == DocumentType.BANK_STATEMENT)
                        .mapToLong(doc -> doc.getMediaFiles().size())
                        .sum() >= 3; // At least 3 months of bank statements
    }
}
