package com.example.charity_app_v1.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class OrganizationUpdateRequest {
    @NotBlank(message = "Le nom est obligatoire")
    @Size(min = 2, max = 100, message = "Le nom doit contenir entre 2 et 100 caractères")
    private String name;

    @Size(max = 1000, message = "La description ne peut pas dépasser 1000 caractères")
    private String description;

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "L'email doit être valide")
    private String email;

    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Le numéro de téléphone doit être valide")
    private String phoneNumber;

    @Pattern(regexp = "^(https?://)?([\\da-z.-]+)\\.([a-z.]{2,6})([/\\w .-]*)*/?$", 
            message = "L'URL du site web doit être valide")
    private String website;

    @Pattern(regexp = "^(https?://)?(www\\.)?facebook\\.com/[\\w.-]+/?$", 
            message = "L'URL Facebook doit être valide")
    private String socialMediaFacebook;

    @Pattern(regexp = "^(https?://)?(www\\.)?twitter\\.com/[\\w.-]+/?$", 
            message = "L'URL Twitter doit être valide")
    private String socialMediaTwitter;

    @Pattern(regexp = "^(https?://)?(www\\.)?instagram\\.com/[\\w.-]+/?$", 
            message = "L'URL Instagram doit être valide")
    private String socialMediaInstagram;

    @Pattern(regexp = "^(https?://)?(www\\.)?linkedin\\.com/(company|in)/[\\w.-]+/?$", 
            message = "L'URL LinkedIn doit être valide")
    private String socialMediaLinkedin;

    @Size(max = 200, message = "L'adresse ne peut pas dépasser 200 caractères")
    private String address;

    @NotBlank(message = "L'adresse légale est obligatoire")
    @Size(max = 200, message = "L'adresse légale ne peut pas dépasser 200 caractères")
    private String legalAddress;
} 