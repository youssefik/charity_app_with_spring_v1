package com.example.charity_app_v1.service;

import com.example.charity_app_v1.dto.OrganizationDTO;
import com.example.charity_app_v1.model.OrganizationStatus;
import java.util.List;
import java.util.Optional;
import com.example.charity_app_v1.model.Organization;
import com.example.charity_app_v1.repository.OrganizationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.charity_app_v1.dto.OrganizationRegistrationRequest;
import com.example.charity_app_v1.dto.OrganizationUpdateRequest;
import com.example.charity_app_v1.exception.ResourceNotFoundException;
import com.example.charity_app_v1.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDateTime;

@Service
public interface OrganizationService {
    OrganizationDTO createOrganization(OrganizationDTO organizationDTO);
    OrganizationDTO updateOrganization(Long id, OrganizationDTO organizationDTO);
    void deleteOrganization(Long id);
    OrganizationDTO getOrganizationById(Long id);
    List<OrganizationDTO> getAllOrganizations();
    List<OrganizationDTO> getOrganizationsByStatus(OrganizationStatus status);
    List<OrganizationDTO> getOrganizationsByAdminId(Long adminId);
    List<OrganizationDTO> getOrganizationsByUser(String email);
    OrganizationDTO approveOrganization(Long id);
    OrganizationDTO rejectOrganization(Long id, String reason);
    OrganizationDTO suspendOrganization(Long id, String suspensionReason);
    OrganizationDTO reactivateOrganization(Long id);
    OrganizationDTO updateOrganizationStatus(Long id, OrganizationStatus status);
    Optional<Organization> findByEmail(String email);
    Organization createOrganization(OrganizationRegistrationRequest request, User user);
    OrganizationDTO updateOrganization(Long id, OrganizationUpdateRequest request);
    void updateLogo(Long id, MultipartFile logo);
    void updateCoverImage(Long id, MultipartFile coverImage);
    void updateBankingInfo(Long id, String bankAccountName, String bankAccountNumber, String bankName, String bankSwift, String paypalEmail);
    Organization verifyOrganization(Long id);
    Organization findById(Long id);
    Organization findByUserId(Long userId);
    List<Organization> findAll();
    Page<Organization> findAll(Pageable pageable);
    List<Organization> findAllVerified();
    List<Organization> searchByName(String name);
    boolean existsByEmail(String email);
    boolean existsByTaxId(String taxId);
    long getTotalActiveOrganizations();
    OrganizationDTO registerOrganization(OrganizationDTO organizationDTO);
}