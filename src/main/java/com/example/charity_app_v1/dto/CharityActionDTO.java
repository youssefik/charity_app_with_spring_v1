package com.example.charity_app_v1.dto;

import com.example.charity_app_v1.model.ActionCategory;
import com.example.charity_app_v1.model.ActionStatus;
import com.example.charity_app_v1.model.Organization;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Data
public class CharityActionDTO {
    private Long id;

    @NotBlank(message = "Le titre est obligatoire")
    private String title;

    @NotBlank(message = "La description est obligatoire")
    private String description;

    @NotNull(message = "La date de début est obligatoire")
    private LocalDateTime startDate;

    @NotNull(message = "La date de fin est obligatoire")
    private LocalDateTime endDate;

    @NotBlank(message = "Le lieu est obligatoire")
    private String location;

    @NotNull(message = "Le montant cible est obligatoire")
    @Positive(message = "Le montant cible doit être positif")
    private BigDecimal targetAmount;

    private BigDecimal currentAmount;

    @NotNull(message = "La catégorie est obligatoire")
    private ActionCategory category;

    private ActionStatus status;
    private Long organizationId;
    private Organization organization;
    private Set<String> mediaUrls;
    private String imageUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean isPopular;
    private boolean isRecommended;
}