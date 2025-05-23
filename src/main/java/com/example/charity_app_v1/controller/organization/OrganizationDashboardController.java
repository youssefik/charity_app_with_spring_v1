package com.example.charity_app_v1.controller.organization;

import com.example.charity_app_v1.dto.CharityActionDTO;
import com.example.charity_app_v1.dto.DonationDTO;
import com.example.charity_app_v1.dto.OrganizationDTO;
import com.example.charity_app_v1.model.OrganizationStatus;
import com.example.charity_app_v1.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/organization/dashboard")
@PreAuthorize("hasRole('ORGANIZATION')")
public class OrganizationDashboardController {
    private static final Logger logger = LoggerFactory.getLogger(OrganizationDashboardController.class);

    private final OrganizationService organizationService;
    private final UserService userService;
    private final CharityActionService charityActionService;
    private final DonationService donationService;
    private final ReportService reportService;

    @Autowired
    public OrganizationDashboardController(
            OrganizationService organizationService,
            UserService userService,
            CharityActionService charityActionService,
            DonationService donationService,
            ReportService reportService) {
        this.organizationService = organizationService;
        this.userService = userService;
        this.charityActionService = charityActionService;
        this.donationService = donationService;
        this.reportService = reportService;
    }

    @GetMapping
    public String dashboard(Model model) {
        try {
            var currentUser = userService.getCurrentUser();
            if (currentUser == null) {
                return "redirect:/login";
            }

            List<OrganizationDTO> organizations = organizationService.getOrganizationsByAdminId(currentUser.getId());
            if (organizations.isEmpty()) {
                return "redirect:/organization/register";
            }

            OrganizationDTO organization = organizations.get(0);
            if (organization.getStatus() != OrganizationStatus.APPROVED) {
                return "redirect:/organization/pending-approval";
            }

            // Récupérer les actions de l'organisation
            List<CharityActionDTO> actions = charityActionService.getActionsByOrganization(organization.getId());
            
            // Récupérer les dons pour chaque action
            Map<Long, List<DonationDTO>> donationsByAction = actions.stream()
                .collect(Collectors.toMap(
                    CharityActionDTO::getId,
                    action -> donationService.getDonationsByActionId(action.getId())
                ));

            // Calculer les statistiques
            Map<String, Object> stats = reportService.getOrganizationStats(organization.getId());

            model.addAttribute("organization", organization);
            model.addAttribute("actions", actions);
            model.addAttribute("donationsByAction", donationsByAction);
            model.addAttribute("stats", stats);

            return "organization/dashboard";
        } catch (Exception e) {
            logger.error("Erreur lors de l'affichage du tableau de bord", e);
            return "redirect:/error?message=" + e.getMessage();
        }
    }

    @GetMapping("/actions-overview")
    public String actions(Model model) {
        try {
            var currentUser = userService.getCurrentUser();
            if (currentUser == null) {
                return "redirect:/login";
            }

            List<OrganizationDTO> organizations = organizationService.getOrganizationsByAdminId(currentUser.getId());
            if (organizations.isEmpty()) {
                return "redirect:/organization/register";
            }

            OrganizationDTO organization = organizations.get(0);
            if (organization.getStatus() != OrganizationStatus.APPROVED) {
                return "redirect:/organization/pending-approval";
            }

            List<CharityActionDTO> actions = charityActionService.getActionsByOrganization(organization.getId());
            model.addAttribute("organization", organization);
            model.addAttribute("actions", actions);

            return "organization/actions/overview";
        } catch (Exception e) {
            logger.error("Erreur lors de l'affichage des actions", e);
            return "redirect:/error?message=" + e.getMessage();
        }
    }

    @GetMapping("/profile")
    public String profile(Model model) {
        try {
            var currentUser = userService.getCurrentUser();
            if (currentUser == null) {
                return "redirect:/login";
            }

            List<OrganizationDTO> organizations = organizationService.getOrganizationsByAdminId(currentUser.getId());
            if (organizations.isEmpty()) {
                return "redirect:/organization/register";
            }

            OrganizationDTO organization = organizations.get(0);
            if (organization.getStatus() != OrganizationStatus.APPROVED) {
                return "redirect:/organization/pending-approval";
            }

            model.addAttribute("organization", organization);
            return "organization/profile";
        } catch (Exception e) {
            logger.error("Erreur lors de l'affichage du profil", e);
            return "redirect:/error?message=" + e.getMessage();
        }
    }
} 