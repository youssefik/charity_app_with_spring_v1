package com.example.charity_app_v1.service;

import com.example.charity_app_v1.dto.DonationDTO;
import com.example.charity_app_v1.model.Donation;
import com.example.charity_app_v1.model.DonationStatus;
import com.example.charity_app_v1.model.Organization;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public interface DonationService {
    List<DonationDTO> getAllDonationsDTO();
    Optional<DonationDTO> getDonationById(Long id);
    DonationDTO createDonation(DonationDTO donationDTO);
    DonationDTO updateDonation(Long id, DonationDTO donationDTO);
    void updateDonationStatus(Long id, DonationStatus status);
    void deleteDonation(Long id);
    List<DonationDTO> getDonationsByUser(Long userId);
    List<DonationDTO> getDonationsByCharityAction(Long charityActionId);
    List<DonationDTO> getDonationsByStatus(DonationStatus status);
    BigDecimal getTotalDonationsForAction(Long charityActionId);
    BigDecimal getTotalDonationsByUser(Long userId);
    void processPayment(DonationDTO donationDTO);
    void refundDonation(Long id);
    List<DonationDTO> getDonationsByOrganization(Long organizationId);
    BigDecimal getTotalDonationsByOrganization(Long organizationId);
    List<DonationDTO> getDonationsByOrganizationAndDateRange(Long organizationId, LocalDate startDate, LocalDate endDate);
    List<DonationDTO> getRecentDonationsByOrganization(Long organizationId, int limit);
    Map<String, BigDecimal> getMonthlyDonationsByOrganization(Long organizationId);
    double getTotalDonationsByOrganization(Organization organization);
    double getMonthlyDonationsByOrganization(Organization organization);
    double getAverageDonationsByOrganization(Organization organization);
    List<Donation> findRecentByOrganization(Organization organization, int limit);
    double getDonationsByOrganizationAndMonth(Organization organization, YearMonth yearMonth);
    List<DonationDTO> getDonationsByActionId(Long actionId);
    List<DonationDTO> getDonationsByDonorId(Long donorId);
    void validateDonation(Long id);
    void rejectDonation(Long id);

    @Transactional
    void processDonation(String paymentId, String payerId, Long actionId, Double amount);
}