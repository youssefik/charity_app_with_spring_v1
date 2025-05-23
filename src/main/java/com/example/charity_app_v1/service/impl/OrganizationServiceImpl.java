package com.example.charity_app_v1.service.impl;

import com.example.charity_app_v1.dto.OrganizationDTO;
import com.example.charity_app_v1.dto.OrganizationRegistrationRequest;
import com.example.charity_app_v1.dto.OrganizationUpdateRequest;
import com.example.charity_app_v1.exception.ResourceNotFoundException;
import com.example.charity_app_v1.model.Organization;
import com.example.charity_app_v1.model.OrganizationStatus;
import com.example.charity_app_v1.model.User;
import com.example.charity_app_v1.repository.OrganizationRepository;
import com.example.charity_app_v1.service.OrganizationService;
import com.example.charity_app_v1.service.UserService;
import com.example.charity_app_v1.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class OrganizationServiceImpl implements OrganizationService {
    private static final Logger logger = LoggerFactory.getLogger(OrganizationServiceImpl.class);

    private final OrganizationRepository organizationRepository;
    private final UserService userService;
    private final EmailService emailService;

    @Override
    public OrganizationDTO createOrganization(OrganizationDTO organizationDTO) {
        logger.info("Début de la création d'une nouvelle organisation : {}", organizationDTO.getName());
        
        // Vérifications préalables
        if (organizationDTO.getRegistrationNumber() == null || organizationDTO.getRegistrationNumber().trim().isEmpty()) {
            logger.error("Le numéro d'enregistrement est vide");
            throw new IllegalArgumentException("Le numéro d'enregistrement est obligatoire");
        }

        // Vérifier si le numéro d'enregistrement existe déjà
        boolean existsByRegNumber = organizationRepository.existsByRegistrationNumber(organizationDTO.getRegistrationNumber());
        logger.info("Vérification du numéro d'enregistrement {} : {}", organizationDTO.getRegistrationNumber(), existsByRegNumber);
        
        if (existsByRegNumber) {
            logger.error("Une organisation avec le numéro d'enregistrement {} existe déjà", organizationDTO.getRegistrationNumber());
            throw new IllegalArgumentException("Une organisation avec ce numéro d'enregistrement existe déjà");
        }

        // Vérifier si l'email existe déjà
        boolean existsByEmail = organizationRepository.existsByEmail(organizationDTO.getEmail());
        logger.info("Vérification de l'email {} : {}", organizationDTO.getEmail(), existsByEmail);
        
        if (existsByEmail) {
            logger.error("Une organisation avec l'email {} existe déjà", organizationDTO.getEmail());
            throw new IllegalArgumentException("Une organisation avec cet email existe déjà");
        }

        // Création de l'organisation
        Organization organization = new Organization();
        updateOrganizationFromDTO(organization, organizationDTO);
        organization.setStatus(OrganizationStatus.PENDING);

        try {
        Organization savedOrganization = organizationRepository.save(organization);
        logger.info("Organisation créée avec succès : {}", savedOrganization.getId());
        return convertToDTO(savedOrganization);
        } catch (Exception e) {
            logger.error("Erreur lors de la sauvegarde de l'organisation", e);
            throw new RuntimeException("Erreur lors de la création de l'organisation : " + e.getMessage());
        }
    }

    @Override
    public Organization createOrganization(OrganizationRegistrationRequest request, User user) {
        Organization organization = new Organization();
        organization.setName(request.getName());
        organization.setDescription(request.getDescription());
        organization.setEmail(request.getEmail());
        organization.setPhoneNumber(request.getPhoneNumber());
        organization.setAddress(request.getAddress());
        organization.setLegalAddress(request.getLegalAddress());
        organization.setWebsite(request.getWebsite());
        organization.setRegistrationNumber(request.getRegistrationNumber());
        organization.setTaxId(request.getTaxId());
        organization.setLegalStatus(request.getLegalStatus());
        organization.setFoundingDate(request.getFoundingDate());
        organization.setStatus(OrganizationStatus.PENDING);
        organization.setUser(user);
        
        return organizationRepository.save(organization);
    }

    @Override
    public OrganizationDTO updateOrganization(Long id, OrganizationDTO organizationDTO) {
        logger.info("Mise à jour de l'organisation : {}", id);
        
        Organization organization = organizationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Organisation non trouvée avec l'id: " + id));

        // Vérifier si le numéro d'enregistrement est déjà utilisé par une autre organisation
        if (!organization.getRegistrationNumber().equals(organizationDTO.getRegistrationNumber()) &&
            organizationRepository.existsByRegistrationNumber(organizationDTO.getRegistrationNumber())) {
            throw new IllegalArgumentException("Une organisation avec ce numéro d'enregistrement existe déjà");
        }

        // Vérifier si l'email est déjà utilisé par une autre organisation
        if (!organization.getEmail().equals(organizationDTO.getEmail()) &&
            organizationRepository.existsByEmail(organizationDTO.getEmail())) {
            throw new IllegalArgumentException("Une organisation avec cet email existe déjà");
        }

        updateOrganizationFromDTO(organization, organizationDTO);
        Organization updatedOrganization = organizationRepository.save(organization);
        logger.info("Organisation mise à jour avec succès : {}", id);
        
        return convertToDTO(updatedOrganization);
    }

    @Override
    public OrganizationDTO updateOrganization(Long id, OrganizationUpdateRequest request) {
        Organization organization = findOrganizationById(id);
        organization.setName(request.getName());
        organization.setDescription(request.getDescription());
        organization.setEmail(request.getEmail());
        organization.setPhoneNumber(request.getPhoneNumber());
        organization.setAddress(request.getAddress());
        organization.setLegalAddress(request.getLegalAddress());
        organization.setWebsite(request.getWebsite());
        organization.setSocialMediaFacebook(request.getSocialMediaFacebook());
        organization.setSocialMediaTwitter(request.getSocialMediaTwitter());
        organization.setSocialMediaInstagram(request.getSocialMediaInstagram());
        organization.setSocialMediaLinkedin(request.getSocialMediaLinkedin());
        
        Organization updatedOrganization = organizationRepository.save(organization);
        return convertToDTO(updatedOrganization);
    }

    @Override
    public void deleteOrganization(Long id) {
        logger.info("Suppression de l'organisation : {}", id);
        
        if (!organizationRepository.existsById(id)) {
            throw new ResourceNotFoundException("Organisation non trouvée avec l'id: " + id);
        }
        organizationRepository.deleteById(id);
        logger.info("Organisation supprimée avec succès : {}", id);
    }

    @Override
    public OrganizationDTO getOrganizationById(Long id) {
        return organizationRepository.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Organization not found with id: " + id));
    }

    @Override
    public List<OrganizationDTO> getAllOrganizations() {
        return organizationRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<OrganizationDTO> getOrganizationsByStatus(OrganizationStatus status) {
        return organizationRepository.findByStatus(status).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<OrganizationDTO> getOrganizationsByAdminId(Long adminId) {
        // Comme findByAdminUserId n'existe pas, nous utilisons une autre approche
        return organizationRepository.findAll().stream()
                .filter(org -> org.getUser() != null && org.getUser().getId().equals(adminId))
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<OrganizationDTO> getOrganizationsByUser(String email) {
        User user = userService.getCurrentUser();
        if (!user.getEmail().equals(email)) {
            throw new ResourceNotFoundException("Utilisateur non trouvé avec l'email: " + email);
        }

        return organizationRepository.findAll().stream()
                .filter(org -> org.getUser() != null && org.getUser().getId().equals(user.getId()))
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public OrganizationDTO approveOrganization(Long id) {
        logger.info("Tentative d'approbation de l'organisation : {}", id);
        
        Organization organization = organizationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Organisation non trouvée avec l'id: " + id));

        if (organization.getStatus() != OrganizationStatus.PENDING) {
            throw new IllegalStateException("Seules les organisations en attente peuvent être approuvées");
        }

        organization.setStatus(OrganizationStatus.APPROVED);
        Organization savedOrg = organizationRepository.save(organization);
        logger.info("Organisation approuvée avec succès : {}", id);

        // Envoi de l'email de confirmation
        try {
            emailService.sendOrganizationApprovalNotification(organization.getEmail(), organization.getName());
            logger.info("Email de confirmation envoyé à : {}", organization.getEmail());
        } catch (Exception e) {
            logger.error("Erreur lors de l'envoi de l'email de confirmation", e);
        }

        return convertToDTO(savedOrg);
    }

    @Override
    public OrganizationDTO rejectOrganization(Long id, String reason) {
        logger.info("Tentative de rejet de l'organisation : {}", id);
        
        Organization organization = organizationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Organisation non trouvée avec l'id: " + id));

        if (organization.getStatus() != OrganizationStatus.PENDING) {
            throw new IllegalStateException("Seules les organisations en attente peuvent être rejetées");
        }

        if (reason == null || reason.trim().isEmpty()) {
            throw new IllegalArgumentException("Une raison de rejet doit être fournie");
        }

        organization.setStatus(OrganizationStatus.REJECTED);
        Organization savedOrg = organizationRepository.save(organization);
        logger.info("Organisation rejetée avec succès : {}", id);

        // Envoi de l'email de rejet
        try {
            emailService.sendOrganizationRejectionNotification(organization.getEmail(), organization.getName(), reason);
            logger.info("Email de rejet envoyé à : {}", organization.getEmail());
        } catch (Exception e) {
            logger.error("Erreur lors de l'envoi de l'email de rejet", e);
        }

        return convertToDTO(savedOrg);
    }

    @Override
    public OrganizationDTO suspendOrganization(Long id, String suspensionReason) {
        logger.info("Tentative de suspension de l'organisation : {}", id);
        
        Organization organization = organizationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Organisation non trouvée avec l'id: " + id));

        if (organization.getStatus() != OrganizationStatus.APPROVED) {
            throw new IllegalStateException("Seules les organisations approuvées peuvent être suspendues");
        }

        if (suspensionReason == null || suspensionReason.trim().isEmpty()) {
            throw new IllegalArgumentException("Une raison de suspension doit être fournie");
        }

        organization.setStatus(OrganizationStatus.SUSPENDED);
        Organization savedOrg = organizationRepository.save(organization);
        logger.info("Organisation suspendue avec succès : {}", id);

        // Envoi de l'email de suspension
        try {
            emailService.sendOrganizationSuspensionNotification(organization.getEmail(), organization.getName(), suspensionReason);
            logger.info("Email de suspension envoyé à : {}", organization.getEmail());
        } catch (Exception e) {
            logger.error("Erreur lors de l'envoi de l'email de suspension", e);
        }

        return convertToDTO(savedOrg);
    }

    @Override
    public OrganizationDTO reactivateOrganization(Long id) {
        logger.info("Tentative de réactivation de l'organisation : {}", id);
        
        Organization organization = organizationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Organisation non trouvée avec l'id: " + id));

        if (organization.getStatus() != OrganizationStatus.SUSPENDED) {
            throw new IllegalStateException("Seules les organisations suspendues peuvent être réactivées");
        }

        organization.setStatus(OrganizationStatus.APPROVED);
        Organization savedOrg = organizationRepository.save(organization);
        logger.info("Organisation réactivée avec succès : {}", id);

        // Envoi de l'email de réactivation
        try {
            emailService.sendOrganizationReactivationNotification(organization.getEmail(), organization.getName());
            logger.info("Email de réactivation envoyé à : {}", organization.getEmail());
        } catch (Exception e) {
            logger.error("Erreur lors de l'envoi de l'email de réactivation", e);
        }

        return convertToDTO(savedOrg);
    }

    @Override
    public OrganizationDTO updateOrganizationStatus(Long id, OrganizationStatus status) {
        Organization organization = organizationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Organisation non trouvée avec l'id: " + id));
        
        organization.setStatus(status);
        return convertToDTO(organizationRepository.save(organization));
    }

    @Override
    public Optional<Organization> findByEmail(String email) {
        return organizationRepository.findByEmail(email);
    }

    @Override
    public void updateLogo(Long id, MultipartFile logo) {
        // Implémentation à faire pour le traitement des fichiers
    }

    @Override
    public void updateCoverImage(Long id, MultipartFile coverImage) {
        // Implémentation à faire pour le traitement des fichiers
    }

    @Override
    public void updateBankingInfo(Long id, String bankAccountName, String bankAccountNumber,
                                        String bankName, String bankSwift, String paypalEmail) {
        Organization organization = findOrganizationById(id);
        organization.setBankAccountName(bankAccountName);
        organization.setBankAccountNumber(bankAccountNumber);
        organization.setBankName(bankName);
        organization.setBankSwift(bankSwift);
        organization.setPaypalEmail(paypalEmail);
        organizationRepository.save(organization);
    }

    @Override
    public Organization verifyOrganization(Long id) {
        Organization organization = findOrganizationById(id);
        organization.setVerified(true);
        organization.setVerificationDate(LocalDateTime.now());
        return organizationRepository.save(organization);
    }

    @Override
    public Organization findById(Long id) {
        return findOrganizationById(id);
    }

    @Override
    public Organization findByUserId(Long userId) {
        return organizationRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Organization not found for user ID: " + userId));
    }

    @Override
    public List<Organization> findAll() {
        return organizationRepository.findAll();
    }

    @Override
    public Page<Organization> findAll(Pageable pageable) {
        return organizationRepository.findAll(pageable);
    }

    @Override
    public List<Organization> findAllVerified() {
        return organizationRepository.findByVerifiedTrue();
    }

    @Override
    public List<Organization> searchByName(String name) {
        return organizationRepository.findByNameContainingIgnoreCase(name);
    }

    @Override
    public boolean existsByEmail(String email) {
        return organizationRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByTaxId(String taxId) {
        return organizationRepository.existsByRegistrationNumber(taxId);
    }

    @Override
    public long getTotalActiveOrganizations() {
        return organizationRepository.findByStatus(OrganizationStatus.ACTIVE).size();
    }

    @Override
    @Transactional
    public OrganizationDTO registerOrganization(OrganizationDTO organizationDTO) {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            throw new RuntimeException("Utilisateur non connecté");
        }

        Organization organization = new Organization();
        updateOrganizationFromDTO(organization, organizationDTO);
        organization.setStatus(OrganizationStatus.PENDING);
        organization.setUser(currentUser);

        Organization savedOrganization = organizationRepository.save(organization);
        return convertToDTO(savedOrganization);
    }

    private Organization findOrganizationById(Long id) {
        return organizationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Organization not found with id: " + id));
    }

    private void updateOrganizationFromDTO(Organization organization, OrganizationDTO dto) {
        organization.setName(dto.getName());
        organization.setDescription(dto.getDescription());
        organization.setEmail(dto.getEmail());
        organization.setPhoneNumber(dto.getPhoneNumber());
        organization.setAddress(dto.getAddress());
        organization.setLegalAddress(dto.getLegalAddress());
        organization.setWebsite(dto.getWebsite());
        organization.setLogoUrl(dto.getLogoUrl());
        organization.setCoverImageUrl(dto.getCoverImageUrl());
        organization.setRegistrationNumber(dto.getRegistrationNumber());
        organization.setTaxId(dto.getTaxId());
        organization.setStatus(dto.getStatus());
        organization.setVerified(dto.isVerified());
        organization.setBankAccountName(dto.getBankAccountName());
        organization.setBankAccountNumber(dto.getBankAccountNumber());
        organization.setBankName(dto.getBankName());
        organization.setBankSwift(dto.getBankSwift());
        organization.setPaypalEmail(dto.getPaypalEmail());
        
        if (dto.getUserId() != null) {
            User adminUser = userService.findById(dto.getUserId());
            organization.setUser(adminUser);
        }
    }

    private OrganizationDTO convertToDTO(Organization organization) {
        OrganizationDTO dto = new OrganizationDTO();
        dto.setId(organization.getId());
        dto.setName(organization.getName());
        dto.setDescription(organization.getDescription());
        dto.setLogoUrl(organization.getLogoUrl());
        dto.setCoverImageUrl(organization.getCoverImageUrl());
        dto.setEmail(organization.getEmail());
        dto.setPhoneNumber(organization.getPhoneNumber());
        dto.setAddress(organization.getAddress());
        dto.setLegalAddress(organization.getLegalAddress());
        dto.setWebsite(organization.getWebsite());
        dto.setSocialMediaFacebook(organization.getSocialMediaFacebook());
        dto.setSocialMediaTwitter(organization.getSocialMediaTwitter());
        dto.setSocialMediaInstagram(organization.getSocialMediaInstagram());
        dto.setSocialMediaLinkedin(organization.getSocialMediaLinkedin());
        dto.setRegistrationNumber(organization.getRegistrationNumber());
        dto.setTaxId(organization.getTaxId());
        dto.setLegalStatus(organization.getLegalStatus());
        dto.setFoundingDate(organization.getFoundingDate());
        dto.setStatus(organization.getStatus());
        dto.setVerified(organization.isVerified());
        dto.setVerificationDate(organization.getVerificationDate());
        dto.setCreatedAt(organization.getCreatedAt());
        dto.setUpdatedAt(organization.getUpdatedAt());
        if (organization.getUser() != null) {
            dto.setUserId(organization.getUser().getId());
        }
        return dto;
    }
}