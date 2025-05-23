package com.example.charity_app_v1.controller.admin;

import com.example.charity_app_v1.dto.CharityActionDTO;
import com.example.charity_app_v1.dto.DonationDTO;
import com.example.charity_app_v1.model.DonationStatus;
import com.example.charity_app_v1.service.CharityActionService;
import com.example.charity_app_v1.service.DonationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/donations")
public class AdminDonationController {

    private final DonationService donationService;
    private final CharityActionService charityActionService;

    @Autowired
    public AdminDonationController(DonationService donationService, CharityActionService charityActionService) {
        this.donationService = donationService;
        this.charityActionService = charityActionService;
    }

    @GetMapping
    public String listDonations(Model model) {
        List<DonationDTO> donations = donationService.getAllDonationsDTO();
        List<CharityActionDTO> charityActions = charityActionService.getAllCharityActions();
        
        model.addAttribute("donations", donations);
        model.addAttribute("charityActions", charityActions);
        return "admin/donations/list";
    }

    @PostMapping("/create")
    public String createDonation(@ModelAttribute DonationDTO donationDTO, RedirectAttributes redirectAttributes) {
        try {
            donationService.createDonation(donationDTO);
            redirectAttributes.addFlashAttribute("success", "Le don a été créé avec succès.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la création du don: " + e.getMessage());
        }
        return "redirect:/admin/donations";
    }

    @GetMapping("/{id}")
    public String viewDonation(@PathVariable Long id, Model model) {
        DonationDTO donation = donationService.getDonationById(id)
                .orElseThrow(() -> new RuntimeException("Don non trouvé"));
        model.addAttribute("donation", donation);
        return "admin/donations/view";
    }

    @PostMapping("/{id}/status")
    public String updateDonationStatus(@PathVariable Long id, 
                                     @RequestParam DonationStatus status,
                                     RedirectAttributes redirectAttributes) {
        try {
            donationService.updateDonationStatus(id, status);
            redirectAttributes.addFlashAttribute("success", "Statut du don mis à jour avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la mise à jour du statut");
        }
        return "redirect:/admin/donations/" + id;
    }

    @PostMapping("/{id}/process")
    public String processDonation(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            DonationDTO donation = donationService.getDonationById(id)
                    .orElseThrow(() -> new RuntimeException("Don non trouvé"));
            donationService.processPayment(donation);
            redirectAttributes.addFlashAttribute("success", "Don traité avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors du traitement du don");
        }
        return "redirect:/admin/donations/" + id;
    }

    @PostMapping("/{id}/refund")
    public String refundDonation(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            donationService.refundDonation(id);
            redirectAttributes.addFlashAttribute("success", "Don remboursé avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors du remboursement du don");
        }
        return "redirect:/admin/donations/" + id;
    }
} 