package com.example.charity_app_v1.service;

/**
 * Interface pour la gestion des emails de l'application
 */
public interface EmailService {
    /**
     * Envoie un email de confirmation d'inscription à une organisation
     * @param to Adresse email du destinataire
     * @param organizationName Nom de l'organisation
     * @param message Message personnalisé
     */
    void sendOrganizationRegistrationConfirmation(String to, String organizationName, String message);

    /**
     * Envoie un email de notification d'approbation à une organisation
     * @param to Adresse email du destinataire
     * @param organizationName Nom de l'organisation
     */
    void sendOrganizationApprovalNotification(String to, String organizationName);

    /**
     * Envoie un email de notification de rejet à une organisation
     * @param to Adresse email du destinataire
     * @param organizationName Nom de l'organisation
     * @param reason Raison du rejet
     */
    void sendOrganizationRejectionNotification(String to, String organizationName, String reason);

    /**
     * Envoie un email de notification de suspension à une organisation
     * @param to Adresse email du destinataire
     * @param organizationName Nom de l'organisation
     * @param reason Raison de la suspension
     */
    void sendOrganizationSuspensionNotification(String to, String organizationName, String reason);

    /**
     * Envoie un email de notification de réactivation à une organisation
     * @param to Adresse email du destinataire
     * @param organizationName Nom de l'organisation
     */
    void sendOrganizationReactivationNotification(String to, String organizationName);
} 