package com.example.charity_app_v1.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class OrganizationRegistrationRequest {
    private String name;
    private String description;
    private String email;
    private String phoneNumber;
    private String address;
    private String legalAddress;
    private String website;
    private String registrationNumber;
    private String taxId;
    private String legalStatus;
    private LocalDateTime foundingDate;
    private String password;
    private String confirmPassword;
} 