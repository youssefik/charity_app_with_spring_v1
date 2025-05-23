package com.example.charity_app_v1.controller.admin;

import com.example.charity_app_v1.dto.CharityActionDTO;
import com.example.charity_app_v1.dto.OrganizationDTO;
import com.example.charity_app_v1.dto.UserDTO;
import com.example.charity_app_v1.model.OrganizationStatus;
import com.example.charity_app_v1.service.OrganizationService;
import com.example.charity_app_v1.service.EmailService;
import com.example.charity_app_v1.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Controller
@RequestMapping("/admin/organizations")
@PreAuthorize("hasRole('ADMIN')")
public class AdminOrganizationController {
    private static final Logger logger = LoggerFactory.getLogger(AdminOrganizationController.class);

    private final OrganizationService organizationService;
    private final EmailService emailService;
    private final UserService userService;

    @Autowired
    public AdminOrganizationController(
            OrganizationService organizationService,
            EmailService emailService,
            UserService userService) {
        this.organizationService = organizationService;
        this.emailService = emailService;
        this.userService = userService;
    }

    @GetMapping
    public String listOrganizations(Model model) {
        List<OrganizationDTO> organizations = organizationService.getAllOrganizations();
        model.addAttribute("organizations", organizations);
        return "admin/organizations/list";
    }

    @GetMapping("/pending")
    public String listPendingOrganizations(Model model) {
        List<OrganizationDTO> pendingOrganizations = organizationService.getOrganizationsByStatus(OrganizationStatus.PENDING);
        model.addAttribute("organizations", pendingOrganizations);
        return "admin/organizations/pending";
    }

    @GetMapping("/{id}")
    public String viewOrganization(@PathVariable Long id, Model model) {
        try {
            OrganizationDTO organization = organizationService.getOrganizationById(id);
            model.addAttribute("organization", organization);
            return "admin/organizations/view";
        } catch (Exception e) {
            throw new RuntimeException("Organisation non trouvée");
        }
    }

    @PostMapping("/{id}/approve")
    public String approveOrganization(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            logger.info("Tentative d'approbation de l'organisation : {}", id);
            OrganizationDTO organization = organizationService.approveOrganization(id);
            logger.info("Organisation approuvée avec succès : {}", id);
            
            redirectAttributes.addFlashAttribute("success", "L'organisation a été approuvée avec succès.");
            return "redirect:/admin/organizations/pending";
        } catch (Exception e) {
            logger.error("Erreur lors de l'approbation de l'organisation : {}", id, e);
            redirectAttributes.addFlashAttribute("error", "Erreur lors de l'approbation de l'organisation : " + e.getMessage());
            return "redirect:/admin/organizations/" + id;
        }
    }

    @PostMapping("/{id}/reject")
    public String rejectOrganization(@PathVariable Long id, @RequestParam String reason, RedirectAttributes redirectAttributes) {
        try {
            logger.info("Tentative de rejet de l'organisation : {}", id);
            OrganizationDTO organization = organizationService.rejectOrganization(id, reason);
            logger.info("Organisation rejetée avec succès : {}", id);
            
            redirectAttributes.addFlashAttribute("success", "L'organisation a été rejetée avec succès.");
            return "redirect:/admin/organizations/pending";
        } catch (Exception e) {
            logger.error("Erreur lors du rejet de l'organisation : {}", id, e);
            redirectAttributes.addFlashAttribute("error", "Erreur lors du rejet de l'organisation : " + e.getMessage());
            return "redirect:/admin/organizations/" + id;
        }
    }

    @PostMapping("/{id}/suspend")
    public String suspendOrganization(@PathVariable Long id, @RequestParam String reason, RedirectAttributes redirectAttributes) {
        try {
            logger.info("Tentative de suspension de l'organisation : {}", id);
            OrganizationDTO organization = organizationService.suspendOrganization(id, reason);
            logger.info("Organisation suspendue avec succès : {}", id);
            
            redirectAttributes.addFlashAttribute("success", "L'organisation a été suspendue avec succès.");
            return "redirect:/admin/organizations";
        } catch (Exception e) {
            logger.error("Erreur lors de la suspension de l'organisation : {}", id, e);
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la suspension de l'organisation : " + e.getMessage());
            return "redirect:/admin/organizations/" + id;
        }
    }

    @PostMapping("/{id}/reactivate")
    public String reactivateOrganization(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            logger.info("Tentative de réactivation de l'organisation : {}", id);
            OrganizationDTO organization = organizationService.reactivateOrganization(id);
            logger.info("Organisation réactivée avec succès : {}", id);
            
            redirectAttributes.addFlashAttribute("success", "L'organisation a été réactivée avec succès.");
            return "redirect:/admin/organizations";
        } catch (Exception e) {
            logger.error("Erreur lors de la réactivation de l'organisation : {}", id, e);
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la réactivation de l'organisation : " + e.getMessage());
            return "redirect:/admin/organizations/" + id;
        }
    }
} 