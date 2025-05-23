package com.example.charity_app_v1.controller.organization;

import com.example.charity_app_v1.dto.OrganizationDTO;
import com.example.charity_app_v1.dto.OrganizationUpdateRequest;
import com.example.charity_app_v1.model.Organization;
import com.example.charity_app_v1.model.OrganizationStatus;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/organization")
@PreAuthorize("hasRole('ORGANIZATION')")
public class OrganizationManagementController {
    private static final Logger logger = LoggerFactory.getLogger(OrganizationManagementController.class);

    private final OrganizationService organizationService;
    private final UserService userService;
    private final CharityActionService charityActionService;
    private final DonationService donationService;
    private final ReportService reportService;

    @Autowired
    public OrganizationManagementController(
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

    @GetMapping("/pending-approval")
    public String pendingApproval(Model model) {
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
            if (organization.getStatus() != OrganizationStatus.PENDING) {
                return "redirect:/organization/dashboard";
            }

            model.addAttribute("organization", organization);
            return "organization/pending-approval";
        } catch (Exception e) {
            return "redirect:/error?message=" + e.getMessage();
        }
    }

    @GetMapping("/rejected")
    public String rejected(Model model) {
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
            if (organization.getStatus() != OrganizationStatus.REJECTED) {
                return "redirect:/organization/dashboard";
            }

            model.addAttribute("organization", organization);
            return "organization/rejected";
        } catch (Exception e) {
            return "redirect:/error?message=" + e.getMessage();
        }
    }

    @GetMapping
    public String showProfile(Model model) {
        String currentUsername = userService.getCurrentUsername();
        Organization organization = organizationService.findByUserId(userService.getCurrentUserId());
        
        if (organization == null) {
            return "redirect:/organization/register";
        }

        model.addAttribute("organization", organization);
        model.addAttribute("updateRequest", new OrganizationUpdateRequest());
        return "organization/profile";
    }

    @PostMapping("/update")
    public String updateProfile(@Valid @ModelAttribute("updateRequest") OrganizationUpdateRequest request,
                              BindingResult bindingResult,
                              RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Veuillez corriger les erreurs dans le formulaire");
            return "redirect:/organization/dashboard/profile";
        }

        try {
            Organization organization = organizationService.findByUserId(userService.getCurrentUserId());
            if (organization == null) {
                return "redirect:/organization/register";
            }

            organizationService.updateOrganization(organization.getId(), request);
            redirectAttributes.addFlashAttribute("success", "Profil mis à jour avec succès");
        } catch (Exception e) {
            logger.error("Erreur lors de la mise à jour du profil", e);
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la mise à jour du profil: " + e.getMessage());
        }

        return "redirect:/organization/dashboard/profile";
    }

    @PostMapping("/logo")
    public String updateLogo(@RequestParam("logo") MultipartFile logo,
                           RedirectAttributes redirectAttributes) {
        try {
            Organization organization = organizationService.findByUserId(userService.getCurrentUserId());
            if (organization == null) {
                return "redirect:/organization/register";
            }

            organizationService.updateLogo(organization.getId(), logo);
            redirectAttributes.addFlashAttribute("success", "Logo mis à jour avec succès");
        } catch (Exception e) {
            logger.error("Erreur lors de la mise à jour du logo", e);
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la mise à jour du logo: " + e.getMessage());
        }

        return "redirect:/organization/dashboard/profile";
    }

    @PostMapping("/cover")
    public String updateCoverImage(@RequestParam("coverImage") MultipartFile coverImage,
                                 RedirectAttributes redirectAttributes) {
        try {
            Organization organization = organizationService.findByUserId(userService.getCurrentUserId());
            if (organization == null) {
                return "redirect:/organization/register";
            }

            organizationService.updateCoverImage(organization.getId(), coverImage);
            redirectAttributes.addFlashAttribute("success", "Image de couverture mise à jour avec succès");
        } catch (Exception e) {
            logger.error("Erreur lors de la mise à jour de l'image de couverture", e);
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la mise à jour de l'image de couverture: " + e.getMessage());
        }

        return "redirect:/organization/dashboard/profile";
    }

    @PostMapping("/banking")
    public String updateBankingInfo(@RequestParam String bankAccountName,
                                  @RequestParam String bankAccountNumber,
                                  @RequestParam String bankName,
                                  @RequestParam String bankSwift,
                                  @RequestParam String paypalEmail,
                                  RedirectAttributes redirectAttributes) {
        try {
            Organization organization = organizationService.findByUserId(userService.getCurrentUserId());
            if (organization == null) {
                return "redirect:/organization/register";
            }

            organizationService.updateBankingInfo(organization.getId(), bankAccountName, bankAccountNumber,
                    bankName, bankSwift, paypalEmail);
            redirectAttributes.addFlashAttribute("success", "Informations bancaires mises à jour avec succès");
        } catch (Exception e) {
            logger.error("Erreur lors de la mise à jour des informations bancaires", e);
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la mise à jour des informations bancaires: " + e.getMessage());
        }

        return "redirect:/organization/dashboard/profile";
    }

    @PostMapping("/social")
    public String updateSocialMedia(@RequestParam(required = false) String facebook,
                                  @RequestParam(required = false) String twitter,
                                  @RequestParam(required = false) String instagram,
                                  @RequestParam(required = false) String linkedin,
                                  RedirectAttributes redirectAttributes) {
        try {
            Organization organization = organizationService.findByUserId(userService.getCurrentUserId());
            if (organization == null) {
                return "redirect:/organization/register";
            }

            OrganizationUpdateRequest request = new OrganizationUpdateRequest();
            request.setSocialMediaFacebook(facebook);
            request.setSocialMediaTwitter(twitter);
            request.setSocialMediaInstagram(instagram);
            request.setSocialMediaLinkedin(linkedin);

            organizationService.updateOrganization(organization.getId(), request);
            redirectAttributes.addFlashAttribute("success", "Réseaux sociaux mis à jour avec succès");
        } catch (Exception e) {
            logger.error("Erreur lors de la mise à jour des réseaux sociaux", e);
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la mise à jour des réseaux sociaux: " + e.getMessage());
        }

        return "redirect:/organization/dashboard/profile";
    }
} 