package com.example.charity_app_v1.repository;

import com.example.charity_app_v1.model.Organization;
import com.example.charity_app_v1.model.OrganizationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrganizationRepository extends JpaRepository<Organization, Long> {
    Optional<Organization> findByEmail(String email);
    
    Optional<Organization> findByUserId(Long userId);
    
    List<Organization> findByStatus(OrganizationStatus status);
    
    List<Organization> findByVerifiedTrue();
    
    List<Organization> findByNameContainingIgnoreCase(String name);
    
    boolean existsByEmail(String email);
    
    boolean existsByRegistrationNumber(String registrationNumber);
}