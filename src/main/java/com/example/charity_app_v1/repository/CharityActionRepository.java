package com.example.charity_app_v1.repository;

import com.example.charity_app_v1.model.CharityAction;
import com.example.charity_app_v1.model.ActionCategory;
import com.example.charity_app_v1.model.ActionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CharityActionRepository extends JpaRepository<CharityAction, Long> {
    List<CharityAction> findByCategory(ActionCategory category);
    List<CharityAction> findByStatus(ActionStatus status);
    List<CharityAction> findByOrganizationId(Long organizationId);
    Long countByOrganizationId(Long organizationId);

    @Query("SELECT ca FROM CharityAction ca WHERE ca.status = 'ACTIVE' AND ca.endDate > CURRENT_TIMESTAMP")
    List<CharityAction> findActiveActions();

    @Query("SELECT ca FROM CharityAction ca WHERE ca.status = 'ACTIVE' AND ca.category = ?1 AND ca.endDate > CURRENT_TIMESTAMP")
    List<CharityAction> findActiveActionsByCategory(ActionCategory category);

    Page<CharityAction> findByOrganizationIdOrderByCreatedAtDesc(Long organizationId, Pageable pageable);
    Long countByOrganizationIdAndStatus(Long organizationId, ActionStatus status);

    @Query("SELECT ca FROM CharityAction ca WHERE ca.currentAmount >= ca.targetAmount * 0.8 ORDER BY ca.currentAmount DESC")
    List<CharityAction> findPopularActions();
    
    @Query("SELECT ca FROM CharityAction ca WHERE ca.status = 'ACTIVE' ORDER BY ca.createdAt DESC")
    List<CharityAction> findRecommendedActions();
    
    @Query("SELECT ca FROM CharityAction ca WHERE ca.category.id = ?1 AND ca.status = 'ACTIVE'")
    List<CharityAction> findActiveActionsByCategory(Long categoryId);

    List<CharityAction> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String title, String description);
    
    long countByStatus(ActionStatus status);
}