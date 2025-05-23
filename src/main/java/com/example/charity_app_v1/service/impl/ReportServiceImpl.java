package com.example.charity_app_v1.service.impl;

import com.example.charity_app_v1.dto.DonationReportDTO;
import com.example.charity_app_v1.model.CharityAction;
import com.example.charity_app_v1.model.Donation;
import com.example.charity_app_v1.model.DonationStatus;
import com.example.charity_app_v1.repository.CharityActionRepository;
import com.example.charity_app_v1.repository.DonationRepository;
import com.example.charity_app_v1.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class ReportServiceImpl implements ReportService {

    private final CharityActionRepository charityActionRepository;
    private final DonationRepository donationRepository;

    @Autowired
    public ReportServiceImpl(CharityActionRepository charityActionRepository,
                           DonationRepository donationRepository) {
        this.charityActionRepository = charityActionRepository;
        this.donationRepository = donationRepository;
    }

    @Override
    public DonationReportDTO getDonationReport(LocalDate startDate, LocalDate endDate) {
        try {
            LocalDateTime startDateTime = startDate.atStartOfDay();
            LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

            List<Donation> donations = donationRepository.findByDateBetween(startDateTime, endDateTime);
            
            DonationReportDTO report = new DonationReportDTO();
            report.setStartDate(startDate);
            report.setEndDate(endDate);
            report.setTotalDonations(donations.size());
            
            BigDecimal totalAmount = donations.stream()
                    .filter(d -> d != null && d.getStatus() == DonationStatus.VALIDATED)
                    .map(Donation::getAmount)
                    .filter(amount -> amount != null)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            report.setAmount(totalAmount);
            return report;
        } catch (Exception e) {
            DonationReportDTO report = new DonationReportDTO();
            report.setStartDate(startDate);
            report.setEndDate(endDate);
            report.setTotalDonations(0);
            report.setAmount(BigDecimal.ZERO);
            return report;
        }
    }

    @Override
    public Map<String, BigDecimal> getDonationsByCategory(LocalDate startDate, LocalDate endDate) {
        try {
            LocalDateTime startDateTime = startDate.atStartOfDay();
            LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

            List<Donation> donations = donationRepository.findByDateBetween(startDateTime, endDateTime);
            
            return donations.stream()
                    .filter(d -> d != null && d.getStatus() == DonationStatus.VALIDATED && d.getAction() != null && d.getAction().getCategory() != null)
                    .collect(Collectors.groupingBy(
                        d -> d.getAction().getCategory().getName(),
                        Collectors.reducing(
                            BigDecimal.ZERO,
                            Donation::getAmount,
                            BigDecimal::add
                        )
                    ));
        } catch (Exception e) {
            return new HashMap<>();
        }
    }

    @Override
    public Map<String, BigDecimal> getDonationsByOrganization(LocalDate startDate, LocalDate endDate) {
        try {
            LocalDateTime startDateTime = startDate.atStartOfDay();
            LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

            List<Donation> donations = donationRepository.findByDateBetween(startDateTime, endDateTime);
            
            return donations.stream()
                    .filter(d -> d != null && d.getStatus() == DonationStatus.VALIDATED && d.getAction() != null && d.getAction().getOrganization() != null)
                    .collect(Collectors.groupingBy(
                        d -> d.getAction().getOrganization().getName(),
                        Collectors.reducing(
                            BigDecimal.ZERO,
                            Donation::getAmount,
                            BigDecimal::add
                        )
                    ));
        } catch (Exception e) {
            return new HashMap<>();
        }
    }

    @Override
    public List<DonationReportDTO> getTopDonors(int limit) {
        try {
            List<Donation> donations = donationRepository.findAll();
            
            Map<Long, DonationReportDTO> donorTotals = new HashMap<>();
            
            donations.stream()
                    .filter(d -> d != null && d.getStatus() == DonationStatus.VALIDATED && d.getUser() != null)
                    .forEach(donation -> {
                        Long userId = donation.getUser().getId();
                        DonationReportDTO report = donorTotals.computeIfAbsent(userId, k -> {
                            DonationReportDTO dto = new DonationReportDTO();
                            dto.setUserId(donation.getUser().getId());
                            dto.setUserName(donation.getUser().getFirstName() + " " + donation.getUser().getLastName());
                            dto.setUserEmail(donation.getUser().getEmail());
                            dto.setAmount(BigDecimal.ZERO);
                            return dto;
                        });
                        report.setAmount(report.getAmount().add(donation.getAmount()));
                    });

            return donorTotals.values().stream()
                    .sorted(Comparator.comparing(DonationReportDTO::getAmount).reversed())
                    .limit(limit)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    @Override
    public List<DonationReportDTO> getTopCharityActions(int limit) {
        try {
            List<Donation> donations = donationRepository.findAll();
            
            Map<Long, DonationReportDTO> actionTotals = new HashMap<>();
            
            donations.stream()
                    .filter(d -> d != null && d.getStatus() == DonationStatus.VALIDATED && d.getAction() != null)
                    .forEach(donation -> {
                        Long actionId = donation.getAction().getId();
                        DonationReportDTO report = actionTotals.computeIfAbsent(actionId, k -> {
                            DonationReportDTO dto = new DonationReportDTO();
                            dto.setActionId(donation.getAction().getId());
                            dto.setActionName(donation.getAction().getTitle());
                            dto.setActionDescription(donation.getAction().getDescription());
                            dto.setCategoryName(donation.getAction().getCategory() != null ? 
                                              donation.getAction().getCategory().getName() : "Non catégorisé");
                            dto.setOrganizationName(donation.getAction().getOrganization() != null ? 
                                                  donation.getAction().getOrganization().getName() : "Organisation inconnue");
                            dto.setAmount(BigDecimal.ZERO);
                            return dto;
                        });
                        report.setAmount(report.getAmount().add(donation.getAmount()));
                    });

            return actionTotals.values().stream()
                    .sorted(Comparator.comparing(DonationReportDTO::getAmount).reversed())
                    .limit(limit)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    @Override
    public Map<String, Object> getOrganizationStats(Long organizationId) {
        Map<String, Object> stats = new HashMap<>();

        // Récupérer toutes les actions de l'organisation
        List<CharityAction> actions = charityActionRepository.findByOrganizationId(organizationId);

        // Calculer le nombre total d'actions
        stats.put("totalActions", actions.size());

        // Calculer le nombre d'actions actives
        long activeActions = actions.stream()
                .filter(action -> action.getStatus().equals("ACTIVE"))
                .count();
        stats.put("activeActions", activeActions);

        // Calculer le nombre d'actions terminées
        long completedActions = actions.stream()
                .filter(action -> action.getStatus().equals("COMPLETED"))
                .count();
        stats.put("completedActions", completedActions);

        // Calculer le montant total des dons
        BigDecimal totalDonations = actions.stream()
                .map(action -> donationRepository.findByActionId(action.getId()))
                .flatMap(List::stream)
                .map(Donation::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        stats.put("totalDonations", totalDonations);

        // Calculer le nombre total de donateurs
        long totalDonors = actions.stream()
                .map(action -> donationRepository.findByActionId(action.getId()))
                .flatMap(List::stream)
                .map(donation -> donation.getUser().getId())
                .distinct()
                .count();
        stats.put("totalDonors", totalDonors);

        // Calculer le montant moyen des dons
        if (totalDonors > 0) {
            BigDecimal averageDonation = totalDonations.divide(BigDecimal.valueOf(totalDonors), 2, BigDecimal.ROUND_HALF_UP);
            stats.put("averageDonation", averageDonation);
        } else {
            stats.put("averageDonation", BigDecimal.ZERO);
        }

        return stats;
    }
}