package com.example.charity_app_v1.service;

import com.example.charity_app_v1.dto.ActionDTO;
import com.example.charity_app_v1.model.Action;
import com.example.charity_app_v1.model.ActionStatus;
import com.example.charity_app_v1.model.Organization;
import com.example.charity_app_v1.repository.ActionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public interface ActionService {
    // Méthodes pour le dashboard
    long countByOrganization(Organization organization);
    long countByOrganizationAndStatus(Organization organization, ActionStatus status);
    List<Action> findRecentByOrganization(Organization organization, int limit);
    Map<ActionStatus, Long> getActionStatusCountsByOrganization(Organization organization);

    // Méthodes CRUD
    List<ActionDTO> getActionsByOrganization(Long organizationId);
    ActionDTO getActionById(Long id);
    ActionDTO createAction(ActionDTO actionDTO, String username);
    ActionDTO updateAction(Long id, ActionDTO actionDTO, String username);
    void deleteAction(Long id);
    List<ActionDTO> getAllActions();
    List<ActionDTO> getActionsByStatus(ActionStatus status);
    List<ActionDTO> getRecentActionsByOrganization(Long organizationId, int limit);
    Map<String, Long> getMonthlyActionsByOrganization(Long organizationId);
    
    // Méthodes de comptage
    Long countActionsByOrganizationAndStatus(Long organizationId, ActionStatus status);
    Long countActionsByOrganization(Long organizationId);
} 