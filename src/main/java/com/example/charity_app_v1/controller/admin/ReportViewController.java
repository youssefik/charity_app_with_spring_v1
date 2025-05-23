package com.example.charity_app_v1.controller.admin;

import com.example.charity_app_v1.dto.DonationReportDTO;
import com.example.charity_app_v1.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin/reports")
@PreAuthorize("hasRole('ADMIN')")
public class ReportViewController {

    private final ReportService reportService;

    @Autowired
    public ReportViewController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping
    public String reportsDashboard(Model model) {
        // Par défaut, afficher les rapports du dernier mois
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusMonths(1);

        // Récupérer les rapports
        DonationReportDTO donationReport = reportService.getDonationReport(startDate, endDate);
        Map<String, BigDecimal> donationsByCategory = reportService.getDonationsByCategory(startDate, endDate);
        Map<String, BigDecimal> donationsByOrganization = reportService.getDonationsByOrganization(startDate, endDate);
        List<DonationReportDTO> topDonors = reportService.getTopDonors(10);
        List<DonationReportDTO> topActions = reportService.getTopCharityActions(10);

        // Calculer les statistiques supplémentaires
        BigDecimal totalAmount = donationReport.getAmount();
        int totalDonations = donationReport.getTotalDonations();
        BigDecimal averageDonation = totalDonations > 0 ? 
            totalAmount.divide(new BigDecimal(totalDonations), 2, BigDecimal.ROUND_HALF_UP) : 
            BigDecimal.ZERO;

        // Ajouter les données au modèle
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("donationReport", donationReport);
        model.addAttribute("donationsByCategory", donationsByCategory);
        model.addAttribute("donationsByOrganization", donationsByOrganization);
        model.addAttribute("topDonors", topDonors);
        model.addAttribute("topActions", topActions);
        model.addAttribute("totalAmount", totalAmount);
        model.addAttribute("totalDonations", totalDonations);
        model.addAttribute("averageDonation", averageDonation);

        return "admin/reports/dashboard";
    }

    @GetMapping("/filter")
    @PreAuthorize("hasRole('ADMIN')")
    public String filterReports(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            Model model) {
        
        // Récupérer les rapports filtrés
        DonationReportDTO donationReport = reportService.getDonationReport(startDate, endDate);
        Map<String, BigDecimal> donationsByCategory = reportService.getDonationsByCategory(startDate, endDate);
        Map<String, BigDecimal> donationsByOrganization = reportService.getDonationsByOrganization(startDate, endDate);
        List<DonationReportDTO> topDonors = reportService.getTopDonors(10);
        List<DonationReportDTO> topActions = reportService.getTopCharityActions(10);

        // Calculer les statistiques supplémentaires
        BigDecimal totalAmount = donationReport.getAmount();
        int totalDonations = donationReport.getTotalDonations();
        BigDecimal averageDonation = totalDonations > 0 ? 
            totalAmount.divide(new BigDecimal(totalDonations), 2, BigDecimal.ROUND_HALF_UP) : 
            BigDecimal.ZERO;

        // Ajouter les données au modèle
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("donationReport", donationReport);
        model.addAttribute("donationsByCategory", donationsByCategory);
        model.addAttribute("donationsByOrganization", donationsByOrganization);
        model.addAttribute("topDonors", topDonors);
        model.addAttribute("topActions", topActions);
        model.addAttribute("totalAmount", totalAmount);
        model.addAttribute("totalDonations", totalDonations);
        model.addAttribute("averageDonation", averageDonation);

        return "admin/reports/dashboard";
    }

    @GetMapping("/export")
    @PreAuthorize("hasRole('ADMIN')")
    public String exportReports(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            Model model) {
        
        // Récupérer les données pour l'export
        DonationReportDTO donationReport = reportService.getDonationReport(startDate, endDate);
        Map<String, BigDecimal> donationsByCategory = reportService.getDonationsByCategory(startDate, endDate);
        Map<String, BigDecimal> donationsByOrganization = reportService.getDonationsByOrganization(startDate, endDate);
        List<DonationReportDTO> topDonors = reportService.getTopDonors(10);
        List<DonationReportDTO> topActions = reportService.getTopCharityActions(10);

        // Ajouter les données au modèle pour l'export
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("donationReport", donationReport);
        model.addAttribute("donationsByCategory", donationsByCategory);
        model.addAttribute("donationsByOrganization", donationsByOrganization);
        model.addAttribute("topDonors", topDonors);
        model.addAttribute("topActions", topActions);

        return "admin/reports/export";
    }
} 