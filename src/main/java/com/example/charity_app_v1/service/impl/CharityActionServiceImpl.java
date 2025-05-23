package com.example.charity_app_v1.service.impl;

import com.example.charity_app_v1.dto.CharityActionDTO;
import com.example.charity_app_v1.exception.ResourceNotFoundException;
import com.example.charity_app_v1.model.ActionCategory;
import com.example.charity_app_v1.model.ActionStatus;
import com.example.charity_app_v1.model.CharityAction;
import com.example.charity_app_v1.model.Organization;
import com.example.charity_app_v1.repository.ActionCategoryRepository;
import com.example.charity_app_v1.repository.CharityActionRepository;
import com.example.charity_app_v1.repository.OrganizationRepository;
import com.example.charity_app_v1.service.CharityActionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CharityActionServiceImpl implements CharityActionService {

    private static final Logger logger = LoggerFactory.getLogger(CharityActionServiceImpl.class);

    @Autowired
    private CharityActionRepository charityActionRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private ActionCategoryRepository actionCategoryRepository;

    @Override
    public CharityActionDTO createCharityAction(CharityActionDTO actionDTO) {
        Organization organization = organizationRepository.findById(actionDTO.getOrganizationId())
                .orElseThrow(() -> new ResourceNotFoundException("Organization not found"));

        ActionCategory category = actionCategoryRepository.findById(actionDTO.getCategory().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        CharityAction action = new CharityAction();
        updateActionFromDTO(action, actionDTO, organization, category);
        action.setStatus(ActionStatus.ACTIVE);

        CharityAction savedAction = charityActionRepository.save(action);
        return convertToDTO(savedAction);
    }

    @Override
    public CharityActionDTO updateCharityAction(Long id, CharityActionDTO actionDTO) {
        CharityAction action = charityActionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Action not found"));

        Organization organization = organizationRepository.findById(actionDTO.getOrganizationId())
                .orElseThrow(() -> new ResourceNotFoundException("Organization not found"));

        ActionCategory category = actionCategoryRepository.findById(actionDTO.getCategory().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        updateActionFromDTO(action, actionDTO, organization, category);
        CharityAction updatedAction = charityActionRepository.save(action);
        return convertToDTO(updatedAction);
    }

    @Override
    public void deleteCharityAction(Long id) {
        if (!charityActionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Action not found with id: " + id);
        }
        charityActionRepository.deleteById(id);
    }

    @Override
    public CharityActionDTO getActionById(Long id) {
        return charityActionRepository.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Action not found"));
    }

    @Override
    public List<CharityActionDTO> getAllCharityActions() {
        logger.info("Récupération de toutes les actions caritatives");
        List<CharityActionDTO> actions = charityActionRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        logger.info("Nombre d'actions trouvées : {}", actions.size());
        return actions;
    }

    @Override
    public List<CharityActionDTO> getCharityActionsByStatus(ActionStatus status) {
        return charityActionRepository.findByStatus(status)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<CharityActionDTO> getActiveCharityActions() {
        return charityActionRepository.findActiveActions()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<CharityActionDTO> getActiveCharityActionsByCategory(ActionCategory category) {
        return charityActionRepository.findActiveActionsByCategory(category)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void updateCharityActionStatus(Long id, ActionStatus status) {
        CharityAction action = charityActionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Action not found with id: " + id));
        action.setStatus(status);
        charityActionRepository.save(action);
    }

    @Override
    public void addMediaToCharityAction(Long id, String mediaUrl) {
        CharityAction action = charityActionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Action not found with id: " + id));
        action.getMediaUrls().add(mediaUrl);
        charityActionRepository.save(action);
    }

    @Override
    public void removeMediaFromCharityAction(Long id, String mediaUrl) {
        CharityAction action = charityActionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Action not found with id: " + id));
        action.getMediaUrls().remove(mediaUrl);
        charityActionRepository.save(action);
    }

    @Override
    public List<CharityActionDTO> getActionsByCategory(Long categoryId) {
        return charityActionRepository.findActiveActionsByCategory(categoryId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<CharityActionDTO> getPopularActions() {
        return charityActionRepository.findPopularActions()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<CharityActionDTO> getRecommendedActions() {
        return charityActionRepository.findRecommendedActions()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<CharityActionDTO> getActionsByOrganization(Long organizationId) {
        return charityActionRepository.findByOrganizationId(organizationId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void archiveAction(Long id) {
        CharityAction action = charityActionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Action not found"));
        action.setStatus(ActionStatus.ARCHIVED);
        charityActionRepository.save(action);
    }

    @Override
    public List<CharityActionDTO> searchActions(String searchTerm) {
        return charityActionRepository.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(searchTerm, searchTerm)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public long getTotalActiveActions() {
        return charityActionRepository.countByStatus(ActionStatus.ACTIVE);
    }

    @Override
    public long getTotalDonations() {
        return charityActionRepository.findAll().stream()
                .mapToLong(action -> action.getDonations().size())
                .sum();
    }

    private void updateActionFromDTO(CharityAction action, CharityActionDTO dto, Organization organization, ActionCategory category) {
        action.setTitle(dto.getTitle());
        action.setDescription(dto.getDescription());
        action.setStartDate(dto.getStartDate());
        action.setEndDate(dto.getEndDate());
        action.setLocation(dto.getLocation());
        action.setTargetAmount(dto.getTargetAmount());
        action.setCurrentAmount(dto.getCurrentAmount());
        action.setCategory(category);
        action.setOrganization(organization);
        action.setMediaUrls(dto.getMediaUrls());
        action.setImageUrl(dto.getImageUrl());
    }

    private CharityActionDTO convertToDTO(CharityAction action) {
        if (action == null) {
            return null;
        }

        logger.info("Conversion de l'action en DTO - ID: {}", action.getId());
        
        CharityActionDTO dto = new CharityActionDTO();
        dto.setId(action.getId());
        dto.setTitle(action.getTitle());
        dto.setDescription(action.getDescription());
        dto.setStartDate(action.getStartDate());
        dto.setEndDate(action.getEndDate());
        dto.setLocation(action.getLocation());
        dto.setTargetAmount(action.getTargetAmount());
        dto.setCurrentAmount(action.getCurrentAmount());
        dto.setCategory(action.getCategory());
        dto.setStatus(action.getStatus());
        
        // Gérer le cas où l'organisation est null
        if (action.getOrganization() != null) {
            dto.setOrganizationId(action.getOrganization().getId());
        } else {
            logger.warn("L'organisation est null pour l'action ID: {}", action.getId());
            dto.setOrganizationId(null);
        }
        
        dto.setMediaUrls(action.getMediaUrls());
        dto.setImageUrl(action.getImageUrl());
        dto.setCreatedAt(action.getCreatedAt());
        dto.setUpdatedAt(action.getUpdatedAt());
        
        // Calculer si l'action est populaire (80% ou plus du montant cible atteint)
        if (action.getCurrentAmount() != null && action.getTargetAmount() != null) {
            dto.setPopular(action.getCurrentAmount().compareTo(action.getTargetAmount().multiply(new java.math.BigDecimal("0.8"))) >= 0);
        } else {
            dto.setPopular(false);
        }
        
        // Une action est recommandée si elle est active et récente
        dto.setRecommended(action.getStatus() == ActionStatus.ACTIVE);
        
        logger.info("Conversion terminée pour l'action ID: {}", action.getId());
        return dto;
    }
}