package com.example.charity_app_v1.repository;

import com.example.charity_app_v1.model.Organization;
import com.example.charity_app_v1.model.Volunteer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VolunteerRepository extends JpaRepository<Volunteer, Long> {
    Long countByOrganizationId(Long organizationId);
    long countByOrganization(Organization organization);
} 