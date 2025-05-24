package com.example.charity_app_v1.controller.organization;

import com.example.charity_app_v1.dto.DonationDTO;
import com.example.charity_app_v1.service.DonationService;
import com.example.charity_app_v1.service.OrganizationService;
import com.example.charity_app_v1.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/organization/donations")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_ORGANIZATION')")
public class OrganizationDonationController {

    private final DonationService donationService;
    private final OrganizationService organizationService;
    private final UserService userService;

    @GetMapping
    public String listDonations(Model model) {
        try {
            Long organizationId = organizationService.findByUserId(userService.getCurrentUserId()).getId();
            List<DonationDTO> donations = donationService.getDonationsByOrganization(organizationId);
            
            model.addAttribute("donations", donations);
            model.addAttribute("totalAmount", donations.stream()
                    .mapToDouble(dto -> dto.getAmount().doubleValue())
                    .sum());
            model.addAttribute("donationCount", donations.size());
            
            return "organization/donations/list";
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des donations", e);
            model.addAttribute("error", "Une erreur est survenue lors de la récupération des donations");
            return "organization/donations/list";
        }
    }

    @GetMapping("/{id}")
    public String viewDonation(@PathVariable Long id, Model model) {
        try {
            Long organizationId = organizationService.findByUserId(userService.getCurrentUserId()).getId();
            DonationDTO donation = donationService.getDonationById(id)
                .orElseThrow(() -> new RuntimeException("Donation not found"));
            
            if (!donation.getActionId().equals(organizationId)) {
                model.addAttribute("error", "Vous n'avez pas accès à cette donation");
                return "redirect:/organization/donations";
            }
            
            model.addAttribute("donation", donation);
            return "organization/donations/view";
        } catch (Exception e) {
            log.error("Erreur lors de la récupération de la donation", e);
            model.addAttribute("error", "Une erreur est survenue lors de la récupération de la donation");
            return "redirect:/organization/donations";
        }
    }

    @PostMapping("/{id}/validate")
    public String validateDonation(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Long organizationId = organizationService.findByUserId(userService.getCurrentUserId()).getId();
            DonationDTO donation = donationService.getDonationById(id)
                .orElseThrow(() -> new RuntimeException("Donation not found"));
            
            if (!donation.getActionId().equals(organizationId)) {
                redirectAttributes.addFlashAttribute("error", "Vous n'avez pas accès à cette donation");
                return "redirect:/organization/donations";
            }
            
            donationService.validateDonation(id);
            redirectAttributes.addFlashAttribute("success", "La donation a été validée avec succès");
        } catch (Exception e) {
            log.error("Erreur lors de la validation de la donation", e);
            redirectAttributes.addFlashAttribute("error", "Une erreur est survenue lors de la validation de la donation");
        }
        return "redirect:/organization/donations";
    }

    @PostMapping("/{id}/reject")
    public String rejectDonation(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Long organizationId = organizationService.findByUserId(userService.getCurrentUserId()).getId();
            DonationDTO donation = donationService.getDonationById(id)
                .orElseThrow(() -> new RuntimeException("Donation not found"));
            
            if (!donation.getActionId().equals(organizationId)) {
                redirectAttributes.addFlashAttribute("error", "Vous n'avez pas accès à cette donation");
                return "redirect:/organization/donations";
            }
            
            donationService.rejectDonation(id);
            redirectAttributes.addFlashAttribute("success", "La donation a été rejetée");
        } catch (Exception e) {
            log.error("Erreur lors du rejet de la donation", e);
            redirectAttributes.addFlashAttribute("error", "Une erreur est survenue lors du rejet de la donation");
        }
        return "redirect:/organization/donations";
    }
} 