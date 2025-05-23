package com.example.charity_app_v1.controller;

import com.example.charity_app_v1.dto.ActionDTO;
import com.example.charity_app_v1.service.ActionService;
import com.example.charity_app_v1.service.DonationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/donation")
public class DonationController {

    private static final Logger log = LoggerFactory.getLogger(DonationController.class);

    @Value("${paypal.client.id}")
    private String paypalClientId;

    private final ActionService actionService;
    private final DonationService donationService;

    @Autowired
    public DonationController(ActionService actionService, DonationService donationService) {
        this.actionService = actionService;
        this.donationService = donationService;
    }

    @GetMapping("/{actionId}")
    public String showDonationPage(@PathVariable Long actionId, Model model) {
        ActionDTO action = actionService.getActionById(actionId);
        if (action == null) {
            return "redirect:/actions?error=Action non trouvée";
        }
        model.addAttribute("action", action);
        model.addAttribute("paypalClientId", paypalClientId);
        return "donation/form";
    }

    @PostMapping("/success")
    public String handleSuccessfulPayment(
            @RequestParam String paymentId,
            @RequestParam String payerId,
            @RequestParam Long actionId,
            @RequestParam Double amount,
            RedirectAttributes redirectAttributes) {
        try {
            log.info("Traitement du paiement - ID: {}, Action: {}, Montant: {}€", paymentId, actionId, amount);
            // Vérifier que l'action existe
            ActionDTO action = actionService.getActionById(actionId);
            if (action == null) {
                throw new RuntimeException("Action non trouvée");
            }
            // Traiter le don
            donationService.processDonation(paymentId, payerId, actionId, amount);
            // Vérifier que le montant a été mis à jour
            ActionDTO updatedAction = actionService.getActionById(actionId);
            log.info("Montant avant: {}€, Montant après: {}€", 
                    action.getCurrentAmount(), 
                    updatedAction.getCurrentAmount());
            redirectAttributes.addFlashAttribute("success", "Votre don a été traité avec succès !");
            return "redirect:/donation/success";
        } catch (Exception e) {
            log.error("Erreur lors du traitement du don", e);
            redirectAttributes.addFlashAttribute("error", "Une erreur est survenue lors du traitement de votre don.");
            return "redirect:/donation/cancel";
        }
    }

    @GetMapping("/success")
    public String showSuccessPage() {
        return "donation/success";
    }

    @GetMapping("/cancel")
    public String showCancelPage() {
        return "donation/cancel";
    }
}