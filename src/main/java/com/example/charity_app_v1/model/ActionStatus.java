package com.example.charity_app_v1.model;

public enum ActionStatus {
    DRAFT("Brouillon"),
    PENDING("En attente"),
    ACTIVE("Active"),
    COMPLETED("Terminée"),
    CANCELLED("Annulée"),
    ARCHIVED("Archivée");

    private final String displayName;

    ActionStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
} 