package com.example.charity_app_v1.dto;

import com.example.charity_app_v1.model.DonationStatus;
import com.example.charity_app_v1.dto.CharityActionDTO;
import lombok.Data;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class DonationDTO {
    private Long id;

    @NotNull(message = "Le montant est obligatoire")
    @Positive(message = "Le montant doit être positif")
    private BigDecimal amount;

    @NotNull(message = "La date de don est obligatoire")
    private LocalDateTime donationDate;

    private DonationStatus status;

    @NotBlank(message = "Le nom du donateur est obligatoire")
    private String donorName;

    @NotBlank(message = "L'email du donateur est obligatoire")
    @Email(message = "L'email doit être valide")
    private String donorEmail;

    private Long actionId;
    private String actionTitle;
    private Long organizationId;
    private String organizationName;
    private Long userId;
    private String message;
    
    private CharityActionDTO charityAction;
    private String transactionId;
    
    public String getTransactionId() {
        return transactionId;
    }
    
    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }
}