package com.charityapp.controller.organization;

import com.charityapp.dto.OrganizationDTO;
import com.charityapp.service.OrganizationService;
import com.charityapp.service.UserService;
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

@Controller
@RequestMapping("/organization")
@PreAuthorize("hasRole('ORGANIZATION')")
public class OrganizationRegistrationController {
    private static final Logger logger = LoggerFactory.getLogger(OrganizationRegistrationController.class);

    private final OrganizationService organizationService;
    private final UserService userService;

    @Autowired
    public OrganizationRegistrationController(OrganizationService organizationService, UserService userService) {
        this.organizationService = organizationService;
        this.userService = userService;
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        logger.info("Affichage du formulaire d'inscription de l'organisation");
        // Vérifier si l'utilisateur a déjà une organisation
        if (!organizationService.getOrganizationsByAdminId(userService.getCurrentUser().getId()).isEmpty()) {
            logger.info("L'utilisateur a déjà une organisation, redirection vers le dashboard");
            return "redirect:/organization/dashboard";
        }

        model.addAttribute("organization", new OrganizationDTO());
        return "organization/register";
    }

    @PostMapping("/register")
    public String registerOrganization(
            @ModelAttribute OrganizationDTO organizationDTO,
            RedirectAttributes redirectAttributes) {
        logger.info("Tentative de création d'une organisation avec les données : {}", organizationDTO);
        try {
            // Vérifier si l'utilisateur est connecté
            if (userService.getCurrentUser() == null) {
                logger.error("Aucun utilisateur connecté");
                redirectAttributes.addFlashAttribute("error", "Vous devez être connecté pour créer une organisation");
                return "redirect:/login";
            }

            // Définir l'administrateur de l'organisation
            organizationDTO.setAdminId(userService.getCurrentUser().getId());
            logger.info("ID de l'administrateur défini : {}", organizationDTO.getAdminId());
            
            // Vérifier si l'utilisateur a déjà une organisation
            if (!organizationService.getOrganizationsByAdminId(organizationDTO.getAdminId()).isEmpty()) {
                logger.info("L'utilisateur a déjà une organisation, redirection vers le dashboard");
                redirectAttributes.addFlashAttribute("error", "Vous avez déjà une organisation");
                return "redirect:/organization/dashboard";
            }

            // Vérifier si le numéro d'identification fiscale existe déjà
            if (organizationService.existsByTaxId(organizationDTO.getTaxIdentificationNumber())) {
                logger.error("Le numéro d'identification fiscale existe déjà : {}", organizationDTO.getTaxIdentificationNumber());
                redirectAttributes.addFlashAttribute("error", "Ce numéro d'identification fiscale est déjà utilisé");
                return "redirect:/organization/register";
            }

            // Vérifier si l'email existe déjà
            if (organizationService.existsByEmail(organizationDTO.getContactEmail())) {
                logger.error("L'email existe déjà : {}", organizationDTO.getContactEmail());
                redirectAttributes.addFlashAttribute("error", "Cet email est déjà utilisé");
                return "redirect:/organization/register";
            }
            
            // Créer l'organisation
            OrganizationDTO createdOrganization = organizationService.createOrganization(organizationDTO);
            logger.info("Organisation créée avec succès : {}", createdOrganization);
            
            redirectAttributes.addFlashAttribute("success", "Votre organisation a été créée avec succès et est en attente d'approbation.");
            return "redirect:/organization/dashboard";
        } catch (Exception e) {
            logger.error("Erreur lors de la création de l'organisation", e);
            redirectAttributes.addFlashAttribute("error", "Une erreur est survenue lors de la création de l'organisation : " + e.getMessage());
            return "redirect:/organization/register";
        }
    }
} 