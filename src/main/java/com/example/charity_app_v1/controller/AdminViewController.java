package com.example.charity_app_v1.controller;

import com.example.charity_app_v1.dto.UserDTO;
import com.example.charity_app_v1.dto.OrganizationDTO;
import com.example.charity_app_v1.dto.CharityActionDTO;
import com.example.charity_app_v1.dto.ActionDTO;
import com.example.charity_app_v1.model.Action;
import com.example.charity_app_v1.model.ActionCategory;
import com.example.charity_app_v1.model.OrganizationStatus;
import com.example.charity_app_v1.model.Role;
import com.example.charity_app_v1.model.ActionStatus;
import com.example.charity_app_v1.service.*;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminViewController {

    private final UserService userService;
    private final RoleService roleService;
    private final ActionService actionService;
    private final CategoryService categoryService;
    private final OrganizationService organizationService;
    private final ReportService reportService;
    private static final Logger logger = LoggerFactory.getLogger(AdminViewController.class);

    @Autowired
    public AdminViewController(UserService userService, 
                             RoleService roleService,
                             ActionService actionService,
                             CategoryService categoryService,
                             OrganizationService organizationService,
                             ReportService reportService) {
        this.userService = userService;
        this.roleService = roleService;
        this.actionService = actionService;
        this.categoryService = categoryService;
        this.organizationService = organizationService;
        this.reportService = reportService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        // Récupérer les statistiques
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", userService.getAllUsers().size());
        stats.put("totalOrganizations", organizationService.getAllOrganizations().size());
        stats.put("pendingOrganizations", organizationService.getOrganizationsByStatus(OrganizationStatus.PENDING).size());
        
        // Statistiques des dons
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(30);
        var donationReport = reportService.getDonationReport(startDate, endDate);
        
        // Utiliser des valeurs par défaut si le rapport est null
        stats.put("totalDonations", donationReport != null ? donationReport.getTotalDonations() : 0);
        stats.put("totalAmount", donationReport != null ? donationReport.getAmount() : BigDecimal.ZERO);
        
        // Statistiques des actions
        stats.put("totalActions", actionService.getAllActions().size());
        stats.put("activeActions", actionService.getAllActions().stream()
            .filter(action -> action.getStatus() == ActionStatus.ACTIVE)
            .count());
        
        // Nouveaux utilisateurs ce mois
        stats.put("newUsersThisMonth", 0); // À implémenter
        
        // État du système
        Map<String, String> systemStatus = new HashMap<>();
        systemStatus.put("storageUsage", "45%");
        systemStatus.put("cpuUsage", "30%");
        systemStatus.put("memoryUsage", "60%");
        
        // Données pour le graphique des dons
        Map<String, Object> donationStats = new HashMap<>();
        donationStats.put("labels", new String[]{"Jan", "Fév", "Mar", "Avr", "Mai", "Jun"});
        donationStats.put("values", new int[]{100, 200, 150, 300, 250, 400});
        
        // Ajouter les données au modèle
        model.addAttribute("stats", stats);
        model.addAttribute("systemStatus", systemStatus);
        model.addAttribute("donationStats", donationStats);
        model.addAttribute("pendingOrganizations", organizationService.getOrganizationsByStatus(OrganizationStatus.PENDING));
        
        return "admin/dashboard";
    }

    @GetMapping("/users")
    public String listUsers(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("roles", roleService.getAllRoles());
        model.addAttribute("userDTO", new UserDTO());
        return "admin/users/list";
    }

    @PostMapping("/users/{id}/enable")
    public String enableUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            userService.enableUser(id);
            redirectAttributes.addFlashAttribute("success", "Utilisateur activé avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de l'activation de l'utilisateur");
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{id}/disable")
    public String disableUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            userService.disableUser(id);
            redirectAttributes.addFlashAttribute("success", "Utilisateur désactivé avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la désactivation de l'utilisateur");
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{id}/delete")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            userService.deleteUser(id);
            redirectAttributes.addFlashAttribute("success", "Utilisateur supprimé avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la suppression de l'utilisateur");
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/users/create")
    @PreAuthorize("hasRole('ADMIN')")
    public String createUser(@ModelAttribute UserDTO userDTO, 
                           @RequestParam Long roleId, 
                           RedirectAttributes redirectAttributes) {
        try {
            if (userService.existsByEmail(userDTO.getEmail())) {
                redirectAttributes.addFlashAttribute("error", "Email déjà utilisé.");
                return "redirect:/admin/users";
            }
            
            Role selectedRole = roleService.getRoleById(roleId)
                .orElseThrow(() -> new IllegalArgumentException("Rôle invalide"));
            
            // Définir les rôles de l'utilisateur
            Set<Role> roles = new HashSet<>();
            roles.add(selectedRole);
            userDTO.setRoles(roles);
            
            userDTO.setActive(true);
            userDTO.setEnabled(true);
            userService.registerUser(userDTO);
            
            redirectAttributes.addFlashAttribute("success", "Utilisateur créé avec succès.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la création de l'utilisateur: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }

    @GetMapping("/actions")
    public String listActions(Model model, @RequestParam(required = false) Long categoryId) {
        List<ActionDTO> actions = actionService.getAllActions();
        logger.info("Actions récupérées : {}", actions.size());
        model.addAttribute("actions", actions);
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("selectedCategoryId", categoryId);
        return "actions/list";
    }

    @PostMapping("/actions/create")
    public String createAction(@RequestParam String title,
                             @RequestParam String description,
                             @RequestParam String location,
                             @RequestParam Long categoryId,
                             @RequestParam Long organizationId,
                             @RequestParam BigDecimal targetAmount,
                             @RequestParam BigDecimal currentAmount,
                             @RequestParam("startDate") String startDateStr,
                             @RequestParam("endDate") String endDateStr,
                             RedirectAttributes redirectAttributes) {
        try {
            // Log des données reçues
            System.out.println("Données reçues :");
            System.out.println("Titre: " + title);
            System.out.println("Description: " + description);
            System.out.println("Lieu: " + location);
            System.out.println("Montant cible: " + targetAmount);
            System.out.println("Montant actuel: " + currentAmount);
            System.out.println("Catégorie ID: " + categoryId);
            System.out.println("Organisation ID: " + organizationId);
            System.out.println("Date début: " + startDateStr);
            System.out.println("Date fin: " + endDateStr);

            // Validation des dates
            LocalDateTime startDate = LocalDate.parse(startDateStr).atStartOfDay();
            LocalDateTime endDate = LocalDate.parse(endDateStr).atStartOfDay();
            
            if (endDate.isBefore(startDate)) {
                redirectAttributes.addFlashAttribute("error", "La date de fin doit être postérieure à la date de début");
                return "redirect:/admin/actions";
            }

            // Récupération et validation de la catégorie
            ActionCategory category = categoryService.getCategoryById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Catégorie invalide"));

            // Création du DTO
            ActionDTO actionDTO = new ActionDTO();
            actionDTO.setTitle(title);
            actionDTO.setDescription(description);
            actionDTO.setTargetAmount(targetAmount);
            actionDTO.setCurrentAmount(currentAmount);
            actionDTO.setCategoryId(categoryId);
            actionDTO.setOrganizationId(organizationId);
            actionDTO.setStartDate(startDate.toLocalDate());
            actionDTO.setEndDate(endDate.toLocalDate());
            actionDTO.setStatus(ActionStatus.ACTIVE);

            // Création de l'action
            actionService.createAction(actionDTO, "admin");
            redirectAttributes.addFlashAttribute("success", "Action créée avec succès");
        } catch (DateTimeParseException e) {
            System.out.println("Erreur de parsing de date: " + e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Format de date invalide");
        } catch (IllegalArgumentException e) {
            System.out.println("Erreur d'argument: " + e.getMessage());
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            System.out.println("Erreur inattendue: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la création de l'action: " + e.getMessage());
        }
        return "redirect:/admin/actions";
    }

    @PostMapping("/actions/{id}/delete")
    public String deleteAction(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            actionService.deleteAction(id);
            redirectAttributes.addFlashAttribute("success", "Action supprimée avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la suppression de l'action: " + e.getMessage());
        }
        return "redirect:/admin/actions";
    }

    @PostMapping("/actions/{id}/update")
    public String updateAction(@PathVariable Long id,
                             @Valid @ModelAttribute ActionDTO actionDTO,
                             @RequestParam Long categoryId,
                             @RequestParam Long organizationId,
                             @RequestParam("startDate") String startDateStr,
                             @RequestParam("endDate") String endDateStr,
                             RedirectAttributes redirectAttributes) {
        try {
            // Validation des dates
            LocalDateTime startDate = LocalDate.parse(startDateStr).atStartOfDay();
            LocalDateTime endDate = LocalDate.parse(endDateStr).atStartOfDay();
            
            if (endDate.isBefore(startDate)) {
                redirectAttributes.addFlashAttribute("error", "La date de fin doit être postérieure à la date de début");
                return "redirect:/admin/actions";
            }

            // Récupération et validation de la catégorie
            ActionCategory category = categoryService.getCategoryById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Catégorie invalide"));

            // Configuration de l'action
            actionDTO.setCategoryId(categoryId);
            actionDTO.setOrganizationId(organizationId);
            actionDTO.setStartDate(startDate.toLocalDate());
            actionDTO.setEndDate(endDate.toLocalDate());

            // Mise à jour de l'action
            actionService.updateAction(id, actionDTO, "admin");
            redirectAttributes.addFlashAttribute("success", "Action mise à jour avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la mise à jour de l'action: " + e.getMessage());
        }
        return "redirect:/admin/actions";
    }

    @GetMapping("/categories")
    public String listCategories(Model model) {
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("newCategory", new ActionCategory());
        return "admin/categories/list";
    }

    @PostMapping("/categories/create")
    public String createCategory(@RequestParam String name,
                               @RequestParam String description,
                               RedirectAttributes redirectAttributes) {
        try {
            categoryService.createCategory(name, description);
            redirectAttributes.addFlashAttribute("success", "Catégorie créée avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la création de la catégorie: " + e.getMessage());
        }
        return "redirect:/admin/categories";
    }

    @PostMapping("/categories/{name}/update")
    public String updateCategory(@PathVariable String name,
                               @RequestParam String newName,
                               @RequestParam String newDescription,
                               RedirectAttributes redirectAttributes) {
        try {
            categoryService.updateCategory(name, newName, newDescription);
            redirectAttributes.addFlashAttribute("success", "Catégorie mise à jour avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la mise à jour de la catégorie: " + e.getMessage());
        }
        return "redirect:/admin/categories";
    }

    @PostMapping("/categories/{name}/delete")
    public String deleteCategory(@PathVariable String name,
                               RedirectAttributes redirectAttributes) {
        try {
            categoryService.deleteCategory(name);
            redirectAttributes.addFlashAttribute("success", "Catégorie supprimée avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la suppression de la catégorie: " + e.getMessage());
        }
        return "redirect:/admin/categories";
    }
} 