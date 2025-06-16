package com.talaty.enums;

import lombok.Data;
import lombok.Getter;

@Getter
public enum RiskLevel {
    LOW("Faible", "LOW"),
    MEDIUM("Moyen", "MEDIUM"),
    HIGH("Élevé", "HIGH"),
    VERY_HIGH("Très Élevé", "VERY_HIGH");

    private final String displayName;
    private final String code;

    RiskLevel(String displayName, String code) {
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
