package com.talaty.enums;

public enum ApplicationStatus {
    DRAFT("Brouillon", "DRAFT"),
    PENDING("En Attente", "PENDING"),
    DOCUMENTS_UPLOADED("Documents Téléchargés", "DOCS_UPLOADED"),
    UNDER_REVIEW("En Cours d'Examen", "UNDER_REVIEW"),
    ADDITIONAL_INFO_REQUIRED("Informations Supplémentaires Requises", "INFO_REQUIRED"),
    SCORING_IN_PROGRESS("Calcul du Score en Cours", "SCORING"),
    APPROVED("Approuvé", "APPROVED"),
    REJECTED("Rejeté", "REJECTED"),
    CANCELLED("Annulé", "CANCELLED");

    private final String displayName;
    private final String code;

    ApplicationStatus(String displayName, String code) {
        this.displayName = displayName;
        this.code = code;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getCode() {
        return code;
    }
}