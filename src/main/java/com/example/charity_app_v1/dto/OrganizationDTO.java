package com.example.charity_app_v1.dto;

import com.example.charity_app_v1.model.OrganizationStatus;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class OrganizationDTO {
    private Long id;
    private String name;
    private String description;
    private String email;
    private String phoneNumber;
    private String address;
    private String website;
    private String logoUrl;
    private String coverImageUrl;
    private OrganizationStatus status;
    private boolean verified;
    private String registrationNumber;
    private String taxId;
    private String bankAccountName;
    private String bankAccountNumber;
    private String bankName;
    private String bankSwift;
    private String paypalEmail;
    private Long userId;
    private String legalAddress;
    private String contactPhone;
    private String mission;
    private String socialMediaFacebook;
    private String socialMediaTwitter;
    private String socialMediaInstagram;
    private String socialMediaLinkedin;
    private String legalStatus;
    private LocalDateTime foundingDate;
    private LocalDateTime verificationDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Pour compatibilité avec le code existant
    public String getContactEmail() {
        return email;
    }

    public void setContactEmail(String email) {
        this.email = email;
    }

    public String getTaxIdentificationNumber() {
        return taxId;
    }

    public void setTaxIdentificationNumber(String taxId) {
        this.taxId = taxId;
    }

    // Méthode pour compatibilité avec le code existant
    public void setAdminUserId(Long userId) {
        this.userId = userId;
    }

    public Long getAdminUserId() {
        return this.userId;
    }

    private String logo;

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }
}