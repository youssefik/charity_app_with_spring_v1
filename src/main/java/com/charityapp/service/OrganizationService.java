package com.charityapp.service;

import com.charityapp.dto.OrganizationDTO;
import com.charityapp.model.OrganizationStatus;
import java.util.List;
import java.util.Optional;

public interface OrganizationService {
    OrganizationDTO createOrganization(OrganizationDTO organizationDTO);
    OrganizationDTO updateOrganization(Long id, OrganizationDTO organizationDTO);
    void deleteOrganization(Long id);
    Optional<OrganizationDTO> getOrganizationById(Long id);
    List<OrganizationDTO> getAllOrganizations();
    List<OrganizationDTO> getOrganizationsByStatus(OrganizationStatus status);
    void updateOrganizationStatus(Long id, OrganizationStatus status);
    boolean existsByTaxId(String taxId);
    boolean existsByEmail(String email);
    List<OrganizationDTO> getOrganizationsByAdminId(Long adminId);
    void activateOrganization(Long id);
    void deactivateOrganization(Long id);
} 