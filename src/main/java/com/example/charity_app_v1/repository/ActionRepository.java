package com.example.charity_app_v1.repository;

import com.example.charity_app_v1.model.Action;
import com.example.charity_app_v1.model.ActionStatus;
import com.example.charity_app_v1.model.Organization;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActionRepository extends JpaRepository<Action, Long> {
    List<Action> findByOrganization(Organization organization);
    List<Action> findByStatus(ActionStatus status);
    List<Action> findByOrganizationAndStatus(Organization organization, ActionStatus status);
    
    @Query("SELECT a FROM Action a WHERE a.organization = :organization ORDER BY a.createdAt DESC")
    List<Action> findTopByOrganizationOrderByCreatedAtDesc(@Param("organization") Organization organization, Pageable pageable);

    @Query("SELECT COUNT(a) FROM Action a WHERE a.organization = :organization")
    long countByOrganization(@Param("organization") Organization organization);

    @Query("SELECT COUNT(a) FROM Action a WHERE a.organization = :organization AND a.status = :status")
    long countByOrganizationAndStatus(@Param("organization") Organization organization, @Param("status") ActionStatus status);

    @Query("SELECT a FROM Action a WHERE a.organization.id = :organizationId")
    List<Action> findByOrganizationId(@Param("organizationId") Long organizationId);

    @Query("SELECT a FROM Action a WHERE a.organization = :organization")
    List<Action> findAllByOrganization(@Param("organization") Organization organization);
} 