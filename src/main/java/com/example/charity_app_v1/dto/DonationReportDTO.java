package com.example.charity_app_v1.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class DonationReportDTO {
    private LocalDate startDate;
    private LocalDate endDate;
    private int totalDonations;
    private BigDecimal amount;
    private String currency = "EUR";
    
    // Champs pour les top donateurs
    private Long userId;
    private String userName;
    private String userEmail;
    
    // Champs pour les top actions
    private Long actionId;
    private String actionName;
    private String actionDescription;
    private String categoryName;
    private String organizationName;
} 