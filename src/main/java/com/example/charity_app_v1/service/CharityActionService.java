package com.example.charity_app_v1.service;

import com.example.charity_app_v1.dto.CharityActionDTO;
import com.example.charity_app_v1.model.ActionCategory;
import com.example.charity_app_v1.model.ActionStatus;
import java.util.List;

public interface CharityActionService {
    // Méthodes principales
    CharityActionDTO createCharityAction(CharityActionDTO actionDTO);
    CharityActionDTO updateCharityAction(Long id, CharityActionDTO actionDTO);
    void deleteCharityAction(Long id);
    CharityActionDTO getActionById(Long id);
    List<CharityActionDTO> getAllCharityActions();
    List<CharityActionDTO> getCharityActionsByStatus(ActionStatus status);
    List<CharityActionDTO> getActiveCharityActions();
    List<CharityActionDTO> getActiveCharityActionsByCategory(ActionCategory category);
    void updateCharityActionStatus(Long id, ActionStatus status);
    void addMediaToCharityAction(Long id, String mediaUrl);
    void removeMediaFromCharityAction(Long id, String mediaUrl);

    // Méthodes pour les fonctionnalités spécifiques
    List<CharityActionDTO> getActionsByCategory(Long categoryId);
    List<CharityActionDTO> getPopularActions();
    List<CharityActionDTO> getRecommendedActions();
    List<CharityActionDTO> getActionsByOrganization(Long organizationId);
    void archiveAction(Long id);
    List<CharityActionDTO> searchActions(String searchTerm);
    long getTotalActiveActions();
    long getTotalDonations();
}