// src/main/java/com/example/charityapp/model/CharityAction.java
package com.example.charity_app_v1.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "charity_actions")
@Data
public class CharityAction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 1000)
    private String description;

    @Column(nullable = false)
    private LocalDateTime startDate;

    @Column(nullable = false)
    private LocalDateTime endDate;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    private BigDecimal targetAmount;

    @Column(nullable = false)
    private BigDecimal currentAmount = BigDecimal.ZERO;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private ActionCategory category;

    @Enumerated(EnumType.STRING)
    private ActionStatus status = ActionStatus.ACTIVE;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "organization_id", nullable = true)
    private Organization organization;

    @OneToMany(mappedBy = "action", cascade = CascadeType.ALL)
    private Set<Donation> donations = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "charity_action_media")
    private Set<String> mediaUrls = new HashSet<>();

    @Column(name = "image_url")
    private String imageUrl;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}