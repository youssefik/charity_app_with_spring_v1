package com.example.charity_app_v1.service;

import com.example.charity_app_v1.dto.DonationReportDTO;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface ReportService {
    DonationReportDTO getDonationReport(LocalDate startDate, LocalDate endDate);
    Map<String, BigDecimal> getDonationsByCategory(LocalDate startDate, LocalDate endDate);
    Map<String, BigDecimal> getDonationsByOrganization(LocalDate startDate, LocalDate endDate);
    List<DonationReportDTO> getTopDonors(int limit);
    List<DonationReportDTO> getTopCharityActions(int limit);
    /**
     * Récupère les statistiques d'une organisation
     * @param organizationId l'ID de l'organisation
     * @return une map contenant les statistiques de l'organisation
     */
    Map<String, Object> getOrganizationStats(Long organizationId);
}