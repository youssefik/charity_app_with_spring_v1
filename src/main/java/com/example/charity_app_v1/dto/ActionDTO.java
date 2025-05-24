package com.example.charity_app_v1.dto;

import com.example.charity_app_v1.model.ActionStatus;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.format.annotation.DateTimeFormat;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ActionDTO {
    private Long id;

    @NotBlank(message = "Le titre est obligatoire")
    private String title;

    @NotBlank(message = "La description est obligatoire")
    private String description;

    @NotNull(message = "Le montant cible est obligatoire")
    @Positive(message = "Le montant cible doit être positif")
    private BigDecimal targetAmount;

    private BigDecimal currentAmount;

    @NotNull(message = "La date de début est obligatoire")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate startDate;

    @NotNull(message = "La date de fin est obligatoire")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate endDate;

    private ActionStatus status;
    private Long organizationId;
    private String organizationName;

    @NotNull(message = "La catégorie est obligatoire")
    private Long categoryId;
    private String categoryName;
    private String imageUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 