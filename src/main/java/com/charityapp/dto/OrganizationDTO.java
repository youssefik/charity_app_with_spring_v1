package com.charityapp.dto;

import com.charityapp.model.OrganizationStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class OrganizationDTO {
    private Long id;

    @NotBlank(message = "Le nom de l'organisation est obligatoire")
    private String name;

    @NotBlank(message = "L'adresse légale est obligatoire")
    private String legalAddress;

    @NotBlank(message = "Le numéro d'identification fiscale est obligatoire")
    @Pattern(regexp = "^[0-9]{14}$", message = "Le numéro d'identification fiscale doit contenir 14 chiffres")
    private String taxIdentificationNumber;

    @NotBlank(message = "L'email de contact est obligatoire")
    @Email(message = "Format d'email invalide")
    private String contactEmail;

    @NotBlank(message = "Le numéro de téléphone est obligatoire")
    @Pattern(regexp = "^[0-9]{10}$", message = "Le numéro de téléphone doit contenir 10 chiffres")
    private String contactPhone;

    private String logo;
    private String description;
    private String website;
    private String mission;
    private OrganizationStatus status;
    private Long adminId;
} 