package com.example.charity_app_v1.controller.organization;

import com.example.charity_app_v1.dto.ActionDTO;
import com.example.charity_app_v1.dto.OrganizationDTO;
import com.example.charity_app_v1.model.ActionCategory;
import com.example.charity_app_v1.model.ActionStatus;
import com.example.charity_app_v1.model.Organization;
import com.example.charity_app_v1.service.ActionService;
import com.example.charity_app_v1.service.ActionCategoryService;
import com.example.charity_app_v1.service.OrganizationService;
import com.example.charity_app_v1.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/organization/dashboard/actions")
@PreAuthorize("hasRole('ORGANIZATION')")
public class OrganizationActionController {
    private static final Logger logger = LoggerFactory.getLogger(OrganizationActionController.class);

    private final ActionService actionService;
    private final OrganizationService organizationService;
    private final UserService userService;
    private final ActionCategoryService actionCategoryService;

    public OrganizationActionController(ActionService actionService,
                                      OrganizationService organizationService,
                                      UserService userService,
                                      ActionCategoryService actionCategoryService) {
        this.actionService = actionService;
        this.organizationService = organizationService;
        this.userService = userService;
        this.actionCategoryService = actionCategoryService;
    }

    @GetMapping
    public String listActions(Model model) {
        String currentUsername = userService.getCurrentUsername();
        List<OrganizationDTO> organizations = organizationService.getOrganizationsByUser(currentUsername);
        
        if (organizations.isEmpty()) {
            return "redirect:/organization/register";
        }

        OrganizationDTO organization = organizations.get(0);
        List<ActionDTO> actions = actionService.getActionsByOrganization(organization.getId());
        
        model.addAttribute("actions", actions);
        model.addAttribute("organization", organization);
        return "organization/actions/list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        logger.info("Affichage du formulaire de création d'action");
        String currentUsername = userService.getCurrentUsername();
        List<OrganizationDTO> organizations = organizationService.getOrganizationsByUser(currentUsername);

        if (organizations.isEmpty()) {
            logger.error("Aucune organisation trouvée pour l'utilisateur : {}", currentUsername);
            return "redirect:/organization/register";
        }

        OrganizationDTO organization = organizations.get(0);
        ActionDTO actionDTO = new ActionDTO();
        actionDTO.setOrganizationId(organization.getId()); // Set the organization ID

        model.addAttribute("action", actionDTO);
        model.addAttribute("organization", organization);
        model.addAttribute("categories", actionCategoryService.getAllCategories());
        model.addAttribute("statuses", Arrays.asList(ActionStatus.values()));

        logger.info("Formulaire de création chargé avec succès");
        return "organization/actions/create";
    }

    @PostMapping("/create")
    public String createAction(@Valid @ModelAttribute("action") ActionDTO actionDTO, BindingResult bindingResult,
                             @AuthenticationPrincipal UserDetails userDetails,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        logger.info("Début de la création d'une action avec les données : {}", actionDTO);
        
        if (bindingResult.hasErrors()) {
            logger.error("Erreurs de validation : {}", bindingResult.getAllErrors());
            String currentUsername = userService.getCurrentUsername();
            List<OrganizationDTO> organizations = organizationService.getOrganizationsByUser(currentUsername);
            
            if (organizations.isEmpty()) {
                logger.error("Aucune organisation trouvée pour l'utilisateur : {}", currentUsername);
                return "redirect:/organization/register";
            }

            OrganizationDTO organization = organizations.get(0);
            model.addAttribute("organization", organization);
            model.addAttribute("categories", actionCategoryService.getAllCategories());
            model.addAttribute("statuses", Arrays.asList(ActionStatus.values()));
            return "organization/actions/create";
        }

        try {
            logger.info("Tentative de création de l'action pour l'utilisateur : {}", userDetails.getUsername());
            // Si le statut n'est pas défini, on le met à DRAFT
            if (actionDTO.getStatus() == null) {
                actionDTO.setStatus(ActionStatus.DRAFT);
            }
            
            // Vérifier que l'organisation existe
            String currentUsername = userService.getCurrentUsername();
            List<OrganizationDTO> organizations = organizationService.getOrganizationsByUser(currentUsername);
            
            if (organizations.isEmpty()) {
                logger.error("Aucune organisation trouvée pour l'utilisateur : {}", currentUsername);
                return "redirect:/organization/register";
            }

            OrganizationDTO organizationDTO = organizations.get(0);
            if (!organizationDTO.getId().equals(actionDTO.getOrganizationId())) {
                logger.error("L'organisation ID {} ne correspond pas à l'organisation de l'utilisateur {}", 
                    actionDTO.getOrganizationId(), currentUsername);
                redirectAttributes.addFlashAttribute("error", "Organisation non autorisée");
                return "redirect:/organization/dashboard/actions";
            }

            ActionDTO createdAction = actionService.createAction(actionDTO, userDetails.getUsername());
            logger.info("Action créée avec succès : {}", createdAction);
            redirectAttributes.addFlashAttribute("success", "Action créée avec succès");
            return "redirect:/organization/dashboard/actions";
        } catch (Exception e) {
            logger.error("Erreur lors de la création de l'action", e);
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la création de l'action: " + e.getMessage());
            return "redirect:/organization/dashboard/actions/create";
        }
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        logger.info("Tentative d'accès au formulaire d'édition pour l'action ID: {}", id);
        
        String currentUsername = userService.getCurrentUsername();
        logger.info("Utilisateur actuel: {}", currentUsername);
        
        List<OrganizationDTO> organizations = organizationService.getOrganizationsByUser(currentUsername);
        logger.info("Organisations trouvées pour l'utilisateur: {}", organizations.size());
        
        if (organizations.isEmpty()) {
            logger.error("Aucune organisation trouvée pour l'utilisateur: {}", currentUsername);
            return "redirect:/organization/register";
        }

        OrganizationDTO organization = organizations.get(0);
        logger.info("Organisation sélectionnée: {}", organization.getId());
        
        try {
        ActionDTO action = actionService.getActionById(id);
            logger.info("Action trouvée: {}", action.getId());
        
        if (!action.getOrganizationId().equals(organization.getId())) {
                logger.error("L'action {} n'appartient pas à l'organisation {}", action.getId(), organization.getId());
            return "redirect:/organization/dashboard/actions";
        }
        
        model.addAttribute("action", action);
        model.addAttribute("organization", organization);
        model.addAttribute("categories", actionCategoryService.getAllCategories());
        model.addAttribute("statuses", Arrays.asList(ActionStatus.values()));
        
            logger.info("Formulaire d'édition chargé avec succès");
        return "organization/actions/edit";
        } catch (Exception e) {
            logger.error("Erreur lors du chargement du formulaire d'édition", e);
            return "redirect:/organization/dashboard/actions";
        }
    }

    @PostMapping("/{id}/update")
    public String updateAction(@PathVariable Long id, @Valid ActionDTO actionDTO, BindingResult bindingResult,
                             @AuthenticationPrincipal UserDetails userDetails,
                             RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Veuillez corriger les erreurs dans le formulaire");
            return "redirect:/organization/dashboard/actions/" + id + "/edit";
        }

        try {
            actionService.updateAction(id, actionDTO, userDetails.getUsername());
            redirectAttributes.addFlashAttribute("success", "Action mise à jour avec succès");
            return "redirect:/organization/dashboard/actions";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la mise à jour de l'action: " + e.getMessage());
            return "redirect:/organization/dashboard/actions/" + id + "/edit";
        }
    }

    @PostMapping("/{id}/delete")
    public String deleteAction(@PathVariable Long id,
                             RedirectAttributes redirectAttributes) {
        String currentUsername = userService.getCurrentUsername();
        List<OrganizationDTO> organizations = organizationService.getOrganizationsByUser(currentUsername);
        
        if (organizations.isEmpty()) {
            return "redirect:/organization/register";
        }

        OrganizationDTO organization = organizations.get(0);
        ActionDTO action = actionService.getActionById(id);
        
        if (!action.getOrganizationId().equals(organization.getId())) {
            return "redirect:/organization/dashboard/actions";
        }
        
        try {
            actionService.deleteAction(id);
            redirectAttributes.addFlashAttribute("success", "Action supprimée avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la suppression de l'action : " + e.getMessage());
        }
        
        return "redirect:/organization/dashboard/actions";
    }
} 