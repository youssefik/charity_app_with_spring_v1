package com.example.charity_app_v1.service.impl;

import com.example.charity_app_v1.dto.ActionDTO;
import com.example.charity_app_v1.dto.OrganizationDTO;
import com.example.charity_app_v1.model.Action;
import com.example.charity_app_v1.model.ActionCategory;
import com.example.charity_app_v1.model.ActionStatus;
import com.example.charity_app_v1.model.CharityAction;
import com.example.charity_app_v1.model.Organization;
import com.example.charity_app_v1.repository.ActionCategoryRepository;
import com.example.charity_app_v1.repository.ActionRepository;
import com.example.charity_app_v1.repository.CharityActionRepository;
import com.example.charity_app_v1.service.ActionService;
import com.example.charity_app_v1.service.OrganizationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.HashMap;
import java.time.YearMonth;

@Service
@Transactional
public class ActionServiceImpl implements ActionService {

    private final ActionRepository actionRepository;
    private final ActionCategoryRepository actionCategoryRepository;
    private final CharityActionRepository charityActionRepository;
    private final OrganizationService organizationService;
    private static final Logger logger = LoggerFactory.getLogger(ActionServiceImpl.class);

    @Autowired
    public ActionServiceImpl(ActionRepository actionRepository, 
                           ActionCategoryRepository actionCategoryRepository,
                           CharityActionRepository charityActionRepository,
                           OrganizationService organizationService) {
        this.actionRepository = actionRepository;
        this.actionCategoryRepository = actionCategoryRepository;
        this.charityActionRepository = charityActionRepository;
        this.organizationService = organizationService;
    }

    @Override
    public List<ActionDTO> getActionsByOrganization(Long organizationId) {
        return actionRepository.findByOrganizationId(organizationId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ActionDTO getActionById(Long id) {
        return actionRepository.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> new RuntimeException("Action non trouvée"));
    }

    @Override
    public ActionDTO createAction(ActionDTO actionDTO, String username) {
        logger.info("Début de la création d'une action dans le service avec les données : {}", actionDTO);
        
        Action action = new Action();
        
        // Récupérer l'organisation
        Organization organization = organizationService.findByEmail(username)
            .orElseThrow(() -> new RuntimeException("Organisation non trouvée pour l'utilisateur : " + username));
        logger.info("Organisation trouvée : {}", organization.getName());
        
        // Récupérer la catégorie
        ActionCategory category = actionCategoryRepository.findById(actionDTO.getCategoryId())
            .orElseThrow(() -> new RuntimeException("Catégorie non trouvée avec l'ID : " + actionDTO.getCategoryId()));
        logger.info("Catégorie trouvée : {}", category.getName());
        
        // Mettre à jour l'action avec les données du DTO
        action.setTitle(actionDTO.getTitle());
        action.setDescription(actionDTO.getDescription());
        action.setTargetAmount(actionDTO.getTargetAmount());
        action.setCurrentAmount(BigDecimal.ZERO);
        action.setActive(true);
        
        // Conversion des dates
        if (actionDTO.getStartDate() != null) {
            action.setStartDate(actionDTO.getStartDate().atStartOfDay());
            logger.info("Date de début définie : {}", action.getStartDate());
        }
        if (actionDTO.getEndDate() != null) {
            action.setEndDate(actionDTO.getEndDate().atTime(LocalTime.MAX));
            logger.info("Date de fin définie : {}", action.getEndDate());
        }
        
        // Définir l'organisation et la catégorie
        action.setOrganization(organization);
        action.setCategory(category);
        
        // Définir le statut
        if (actionDTO.getStatus() != null) {
            action.setStatus(actionDTO.getStatus());
        } else {
            action.setStatus(ActionStatus.DRAFT);
        }
        logger.info("Statut défini : {}", action.getStatus());
        
        // Définir l'URL de l'image si présente
        if (actionDTO.getImageUrl() != null && !actionDTO.getImageUrl().isEmpty()) {
            action.setImageUrl(actionDTO.getImageUrl());
        }
        
        logger.info("Action préparée pour la sauvegarde : {}", action);
        
        try {
        // Sauvegarder l'action
        action = actionRepository.save(action);
            logger.info("Action sauvegardée avec succès, ID : {}", action.getId());
        return convertToDTO(action);
        } catch (Exception e) {
            logger.error("Erreur lors de la sauvegarde de l'action", e);
            throw new RuntimeException("Erreur lors de la sauvegarde de l'action : " + e.getMessage());
        }
    }

    @Override
    public ActionDTO updateAction(Long id, ActionDTO actionDTO, String username) {
        Action action = actionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Action non trouvée"));
        updateActionFromDTO(action, actionDTO);
        action = actionRepository.save(action);
        return convertToDTO(action);
    }

    @Override
    public void deleteAction(Long id) {
        actionRepository.deleteById(id);
    }

    @Override
    public List<ActionDTO> getAllActions() {
        return actionRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ActionDTO> getActionsByStatus(ActionStatus status) {
        return actionRepository.findByStatus(status).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ActionDTO> getRecentActionsByOrganization(Long organizationId, int limit) {
        try {
            OrganizationDTO organizationDTO = organizationService.getOrganizationById(organizationId);
        
        Organization organization = new Organization();
        organization.setId(organizationDTO.getId());
        organization.setName(organizationDTO.getName());
        organization.setStatus(organizationDTO.getStatus());
        
        return actionRepository.findTopByOrganizationOrderByCreatedAtDesc(
            organization,
            PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "createdAt"))
        ).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Organisation non trouvée");
        }
    }

    @Override
    public Long countActionsByOrganizationAndStatus(Long organizationId, ActionStatus status) {
        return charityActionRepository.countByOrganizationIdAndStatus(organizationId, status);
    }

    @Override
    public Long countActionsByOrganization(Long organizationId) {
        return charityActionRepository.countByOrganizationId(organizationId);
    }

    @Override
    public Map<String, Long> getMonthlyActionsByOrganization(Long organizationId) {
        return actionRepository.findByOrganizationId(organizationId).stream()
                .collect(Collectors.groupingBy(
                    action -> action.getCreatedAt().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM")),
                    Collectors.counting()
                ));
    }

    @Override
    public long countByOrganization(Organization organization) {
        return actionRepository.countByOrganization(organization);
    }

    @Override
    public long countByOrganizationAndStatus(Organization organization, ActionStatus status) {
        return actionRepository.countByOrganizationAndStatus(organization, status);
    }

    @Override
    public List<Action> findRecentByOrganization(Organization organization, int limit) {
        return actionRepository.findTopByOrganizationOrderByCreatedAtDesc(
            organization,
            PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "createdAt"))
        );
    }

    @Override
    public Map<ActionStatus, Long> getActionStatusCountsByOrganization(Organization organization) {
        return actionRepository.findAllByOrganization(organization).stream()
                .collect(Collectors.groupingBy(
                    Action::getStatus,
                    Collectors.counting()
                ));
    }

    private ActionDTO convertToDTO(Action action) {
        ActionDTO dto = new ActionDTO();
        dto.setId(action.getId());
        dto.setTitle(action.getTitle());
        dto.setDescription(action.getDescription());
        dto.setTargetAmount(action.getTargetAmount());
        dto.setCurrentAmount(action.getCurrentAmount());
        dto.setStatus(action.getStatus());
        dto.setOrganizationId(action.getOrganization().getId());
        dto.setCategoryId(action.getCategory().getId());
        dto.setCreatedAt(action.getCreatedAt());
        dto.setUpdatedAt(action.getUpdatedAt());
        return dto;
    }

    private void updateActionFromDTO(Action action, ActionDTO dto) {
        action.setTitle(dto.getTitle());
        action.setDescription(dto.getDescription());
        action.setTargetAmount(dto.getTargetAmount());
        action.setCurrentAmount(dto.getCurrentAmount() != null ? dto.getCurrentAmount() : BigDecimal.ZERO);
        action.setStatus(dto.getStatus());
        
        // Convertir LocalDate en LocalDateTime
        if (dto.getStartDate() != null) {
            action.setStartDate(dto.getStartDate().atStartOfDay());
        }
        if (dto.getEndDate() != null) {
            action.setEndDate(dto.getEndDate().atTime(LocalTime.MAX));
        }
        
        action.setImageUrl(dto.getImageUrl());
    }
} 