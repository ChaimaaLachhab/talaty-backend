package com.talaty.enums;

import lombok.Getter;

@Getter
public enum ScoreCategory {
    IDENTITY("Identité", 25),
    BUSINESS("Entreprise", 25),
    FINANCIAL("Financier", 30),
    BANKING("Bancaire", 20);

    private final String displayName;
    private final int maxScore;

    ScoreCategory(String displayName, int maxScore) {
        this.displayName = displayName;
        this.maxScore = maxScore;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getMaxScore() {
        return maxScore;
    }
}
