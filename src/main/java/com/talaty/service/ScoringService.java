package com.talaty.service;

import com.talaty.enums.DocumentType;
import com.talaty.model.EKYC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
public class ScoringService {
    private final Logger logger = LoggerFactory.getLogger(ScoringService.class);

    public void calculateInitialScore(EKYC ekyc) {
        int score = 0;
        int maxScore = 100;

        // Informations de base de l'entreprise (30 points)
        score += calculateCompanyInfoScore(ekyc);

        // Informations financières (25 points)
        score += calculateFinancialScore(ekyc);

        // Documents soumis (25 points)
        score += calculateDocumentScore(ekyc);

        // Cohérence des données (20 points)
        score += calculateConsistencyScore(ekyc);

        ekyc.setScore(Math.min(score, maxScore));
        ekyc.setMaxScore(maxScore);

        logger.info("Score initial calculé: {}/{} pour eKYC {}", score, maxScore, ekyc.getId());
    }

    public void calculateFinalScore(EKYC ekyc) {
        calculateInitialScore(ekyc); // Recalculer avec toutes les données

        // Bonus pour soumission complète
        if (ekyc.areRequiredDocumentsSubmitted()) {
            ekyc.setScore(Math.min(ekyc.getScore() + 5, ekyc.getMaxScore()));
        }

        logger.info("Score final calculé: {}/{} pour eKYC {}", ekyc.getScore(), ekyc.getMaxScore(), ekyc.getId());
    }

    private int calculateCompanyInfoScore(EKYC ekyc) {
        int score = 0;

        // Informations complètes (15 points)
        if (ekyc.getCompanyName() != null && !ekyc.getCompanyName().trim().isEmpty()) score += 3;
        if (ekyc.getBusinessSector() != null && !ekyc.getBusinessSector().trim().isEmpty()) score += 3;
        if (ekyc.getBusinessPurpose() != null && !ekyc.getBusinessPurpose().trim().isEmpty()) score += 3;
        if (ekyc.getCompanyRegistrationNumber() != null) score += 3;
        if (ekyc.getAddress() != null && !ekyc.getAddress().trim().isEmpty()) score += 3;

        // Ancienneté entreprise (15 points)
        if (ekyc.getCompanyEstablishedDate() != null) {
            long years = ChronoUnit.YEARS.between(ekyc.getCompanyEstablishedDate(), LocalDate.now());
            if (years >= 5) score += 15;
            else if (years >= 2) score += 10;
            else if (years >= 1) score += 5;
        }

        return Math.min(score, 30);
    }

    private int calculateFinancialScore(EKYC ekyc) {
        int score = 0;

        // Revenus déclarés (15 points)
        if (ekyc.getMonthlyRevenue() != null && ekyc.getAnnualRevenue() != null) {
            double expectedAnnual = ekyc.getMonthlyRevenue() * 12;
            double ratio = ekyc.getAnnualRevenue() / expectedAnnual;

            if (ratio >= 0.8 && ratio <= 1.2) {
                score += 15; // Cohérence parfaite
            } else if (ratio >= 0.6 && ratio <= 1.4) {
                score += 10; // Cohérence acceptable
            } else if (ratio >= 0.4 && ratio <= 1.6) {
                score += 5; // Cohérence faible
            }
        }

        // Taille de l'entreprise (10 points)
        if (ekyc.getNumberOfEmployees() != null) {
            if (ekyc.getNumberOfEmployees() >= 10) score += 10;
            else if (ekyc.getNumberOfEmployees() >= 5) score += 7;
            else if (ekyc.getNumberOfEmployees() >= 2) score += 5;
            else score += 2;
        }

        return Math.min(score, 25);
    }

    private int calculateDocumentScore(EKYC ekyc) {
        int score = 0;

        // Documents obligatoires
        if (ekyc.isDocumentSubmitted(DocumentType.NATIONAL_ID)) score += 8;
        if (ekyc.isDocumentSubmitted(DocumentType.COMPANY_REGISTRATION)) score += 12;

        // Relevés bancaires (minimum 3 mois)
        long bankStatements = ekyc.getDocuments().stream()
                .filter(doc -> doc.getType() == DocumentType.BANK_STATEMENT)
                .mapToLong(doc -> doc.getMediaFiles().size())
                .sum();

        if (bankStatements >= 3) score += 5;
        else score += (int) bankStatements;

        return Math.min(score, 25);
    }

    private int calculateConsistencyScore(EKYC ekyc) {
        int score = 0;

        // Cohérence informations bancaires
        if (ekyc.getBankName() != null && ekyc.getBankAccountNumber() != null) {
            score += 10;
        }

        // Cohérence montant demandé vs revenus
        if (ekyc.getRequestedCreditAmount() != null && ekyc.getMonthlyRevenue() != null) {
            double ratio = ekyc.getRequestedCreditAmount() / (ekyc.getMonthlyRevenue() * 12);
            if (ratio <= 0.5) score += 10; // Demande raisonnable
            else if (ratio <= 1.0) score += 7;
            else if (ratio <= 2.0) score += 3;
        }

        return Math.min(score, 20);
    }
}

