package com.talaty.enums;

import lombok.Getter;

@Getter
public enum DocumentType {
    // Identity Documents
    NATIONAL_ID("Carte d'Identité Nationale"),
    PASSPORT("Passeport"),
    DRIVING_LICENSE("Permis de Conduire"),

    // Business Documents
    COMPANY_REGISTRATION("Registre de Commerce"),
    BUSINESS_LICENSE("Licence d'Activité"),
    TAX_CERTIFICATE("Certificat Fiscal"),

    // Financial Documents
    BANK_STATEMENT("Relevé Bancaire"),
    FINANCIAL_STATEMENT("État Financier"),
    INCOME_PROOF("Justificatif de Revenus"),

    // Additional Documents
    UTILITY_BILL("Facture de Services Publics"),
    RENTAL_AGREEMENT("Contrat de Location"),
    OTHER("Autre");

    private final String displayName;

    DocumentType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
