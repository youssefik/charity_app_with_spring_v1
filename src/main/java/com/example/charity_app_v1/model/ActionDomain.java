package com.example.charity_app_v1.model;

public enum ActionDomain {
    EDUCATION("Éducation"),
    HEALTH("Santé"),
    ENVIRONMENT("Environnement"),
    SOCIAL("Social"),
    CULTURE("Culture"),
    SPORTS("Sports"),
    OTHER("Autre");

    private final String displayName;

    ActionDomain(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
} 