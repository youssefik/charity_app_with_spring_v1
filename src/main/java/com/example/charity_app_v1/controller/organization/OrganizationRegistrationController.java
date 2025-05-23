package com.example.charity_app_v1.controller.organization;

import com.example.charity_app_v1.dto.OrganizationDTO;
import com.example.charity_app_v1.model.OrganizationStatus;
import com.example.charity_app_v1.service.OrganizationService;
import com.example.charity_app_v1.service.UserService;
import com.example.charity_app_v1.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/organization")
@PreAuthorize("hasRole('ORGANIZATION')")
public class OrganizationRegistrationController {
    private static final Logger logger = LoggerFactory.getLogger(OrganizationRegistrationController.class);

    private final OrganizationService organizationService;
    private final UserService userService;
    private final EmailService emailService;

    @Autowired
    public OrganizationRegistrationController(
            OrganizationService organizationService, 
            UserService userService,
            EmailService emailService) {
        this.organizationService = organizationService;
        this.userService = userService;
        this.emailService = emailService;
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        logger.info("Affichage du formulaire d'inscription de l'organisation");
        try {
            // Vérifier si l'utilisateur a déjà une organisation
            var currentUser = userService.getCurrentUser();
            if (currentUser == null) {
                logger.error("Aucun utilisateur connecté");
                return "redirect:/login";
            }

            List<OrganizationDTO> organizations = organizationService.getOrganizationsByAdminId(currentUser.getId());
            if (!organizations.isEmpty()) {
                logger.info("L'utilisateur a déjà une organisation, redirection vers le dashboard");
                return "redirect:/organization/dashboard";
            }

            model.addAttribute("organization", new OrganizationDTO());
            return "organization/register";
        } catch (Exception e) {
            logger.error("Erreur lors de l'affichage du formulaire d'inscription", e);
            return "redirect:/error?message=" + e.getMessage();
        }
    }

    @PostMapping("/register")
    public String registerOrganization(
            @ModelAttribute OrganizationDTO organizationDTO,
            RedirectAttributes redirectAttributes) {
        logger.info("Tentative de création d'une organisation avec les données : {}", organizationDTO);
        try {
            // Vérifier si l'utilisateur est connecté
            var currentUser = userService.getCurrentUser();
            if (currentUser == null) {
                redirectAttributes.addFlashAttribute("error", "Vous devez être connecté pour enregistrer une organisation.");
                return "redirect:/login";
            }

            // Vérifier si l'utilisateur a déjà une organisation
            if (!organizationService.getOrganizationsByAdminId(currentUser.getId()).isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Vous avez déjà une organisation enregistrée.");
                return "redirect:/organization/dashboard";
            }

            // Définir l'ID de l'administrateur
            organizationDTO.setAdminUserId(currentUser.getId());
            organizationDTO.setStatus(OrganizationStatus.PENDING);

            // Créer l'organisation
            OrganizationDTO createdOrganization = organizationService.createOrganization(organizationDTO);
            logger.info("Organisation créée avec succès : {}", createdOrganization);

            // Envoyer un email de confirmation
            emailService.sendOrganizationRegistrationConfirmation(
                currentUser.getEmail(),
                createdOrganization.getName(),
                "Votre demande d'inscription a été reçue et est en attente d'approbation par un administrateur."
            );

            redirectAttributes.addFlashAttribute("success", "Votre organisation a été créée avec succès et est en attente d'approbation.");
            return "redirect:/organization/pending-approval";
        } catch (Exception e) {
            logger.error("Erreur lors de la création de l'organisation", e);
            redirectAttributes.addFlashAttribute("error", "Erreur lors de l'enregistrement de l'organisation : " + e.getMessage());
            return "redirect:/organization/register";
        }
    }
}