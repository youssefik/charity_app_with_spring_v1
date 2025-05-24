package com.example.charity_app_v1.controller;

import com.example.charity_app_v1.dto.DonationDTO;
import com.example.charity_app_v1.model.DonationStatus;

import com.example.charity_app_v1.service.DonationService;
import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.math.BigDecimal;

@Controller
@RequestMapping("/paypal")
public class PaymentController {

    @Autowired
    private DonationService donationService;

    @Autowired
    private com.example.charity_app_v1.service.PayPalService payPalService;

    @PostMapping("/create-payment")
    public RedirectView createPayment(@RequestParam("donationId") Long donationId) {
        try {
            DonationDTO donation = donationService.getDonationById(donationId).orElseThrow();
            
            Payment payment = payPalService.createPayment(
            donation.getAmount().doubleValue(),
                "USD",
                "paypal",
                "sale",
                "Donation to " + donation.getCharityAction().getTitle(),
                "http://localhost:8080/paypal/cancel",
                "http://localhost:8080/paypal/success"
            );
            
            for(Links link: payment.getLinks()) {
                if(link.getRel().equals("approval_url")) {
                    return new RedirectView(link.getHref());
                }
            }
            
        } catch (PayPalRESTException | RuntimeException e) {
            e.printStackTrace();
            // Handle error and redirect to error page
            return new RedirectView("/error");
        }
        
        return new RedirectView("/error");
    }

    @GetMapping("/success")
    public String paymentSuccess(@RequestParam("paymentId") String paymentId, 
                                @RequestParam("PayerID") String payerId) {
        try {
            Payment payment = payPalService.executePayment(paymentId, payerId);
            
            if (payment.getState().equals("approved")) {
                // Find the donation by payment ID and update status
                // For demonstration, we'll assume the donation ID is stored in session
                Long donationId = getDonationIdFromPayment(paymentId);
                DonationDTO donation = donationService.getDonationById(donationId).orElseThrow();
                
                donation.setStatus(DonationStatus.VALIDATED);
                donation.setTransactionId(paymentId);
                donationService.updateDonation(donationId, donation);
                
                return "redirect:/donation/success?paymentId=" + paymentId;
            }
        } catch (PayPalRESTException e) {
            e.printStackTrace();
        }
        
        return "redirect:/donation/failure";
    }

    @GetMapping("/cancel")
    public String paymentCancel() {
        return "redirect:/donation/cancel";
    }

    // Helper method to find donation by payment ID
    private Long getDonationIdFromPayment(String paymentId) {
        // In a real application, you would look up the donation ID from your database
        return 1L; // Replace with actual implementation
    }
}
