package com.example.charity_app_v1.service.impl;

import com.example.charity_app_v1.dto.CharityActionDTO;
import com.example.charity_app_v1.dto.DonationDTO;
import com.example.charity_app_v1.exception.ResourceNotFoundException;
import com.example.charity_app_v1.model.CharityAction;
import com.example.charity_app_v1.model.Donation;
import com.example.charity_app_v1.model.DonationStatus;
import com.example.charity_app_v1.model.Organization;
import com.example.charity_app_v1.model.User;
import com.example.charity_app_v1.repository.CharityActionRepository;
import com.example.charity_app_v1.repository.DonationRepository;
import com.example.charity_app_v1.repository.UserRepository;
import com.example.charity_app_v1.service.DonationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DonationServiceImpl implements DonationService {

    private final DonationRepository donationRepository;
    private final UserRepository userRepository;
    private final CharityActionRepository charityActionRepository;

    @Override
    public List<DonationDTO> getAllDonationsDTO() {
        return donationRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<DonationDTO> getDonationById(Long id) {
        return donationRepository.findById(id)
                .map(this::convertToDTO);
    }

    @Override
    @Transactional
    public void validateDonation(Long id) {
        Donation donation = donationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Donation non trouvée"));
        
        donation.setStatus(DonationStatus.VALIDATED);
        donationRepository.save(donation);
        log.info("Donation {} validée", id);
    }

    @Override
    @Transactional
    public void rejectDonation(Long id) {
        Donation donation = donationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Donation non trouvée"));
        
        donation.setStatus(DonationStatus.REJECTED);
        donationRepository.save(donation);
        log.info("Donation {} rejetée", id);
    }

    @Override
    @Transactional
    public DonationDTO createDonation(DonationDTO donationDTO) {
        Donation donation = new Donation();
        updateDonationFromDTO(donation, donationDTO);
        donation.setStatus(DonationStatus.PENDING);

        if (donationDTO.getActionId() != null) {
            CharityAction action = charityActionRepository.findById(donationDTO.getActionId())
                    .orElseThrow(() -> new ResourceNotFoundException("Action caritative non trouvée avec l'id: " + donationDTO.getActionId()));
            donation.setAction(action);
        }

        Donation savedDonation = donationRepository.save(donation);
        return convertToDTO(savedDonation);
    }

    @Override
    public DonationDTO updateDonation(Long id, DonationDTO donationDTO) {
        Donation donation = donationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Don non trouvé avec l'id: " + id));

        if (donationDTO.getActionId() != null) {
            CharityAction action = charityActionRepository.findById(donationDTO.getActionId())
                    .orElseThrow(() -> new ResourceNotFoundException("Action caritative non trouvée avec l'id: " + donationDTO.getActionId()));
            donation.setAction(action);
        }

        updateDonationFromDTO(donation, donationDTO);
        Donation updatedDonation = donationRepository.save(donation);
        return convertToDTO(updatedDonation);
    }

    @Override
    public void updateDonationStatus(Long id, DonationStatus status) {
        Donation donation = donationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Don non trouvé avec l'id: " + id));
        donation.setStatus(status);
        donationRepository.save(donation);
    }

    @Override
    public void deleteDonation(Long id) {
        if (!donationRepository.existsById(id)) {
            throw new ResourceNotFoundException("Don non trouvé avec l'id: " + id);
        }
        donationRepository.deleteById(id);
    }

    @Override
    public List<DonationDTO> getDonationsByUser(Long userId) {
        return donationRepository.findByUserId(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<DonationDTO> getDonationsByCharityAction(Long actionId) {
        return donationRepository.findByActionId(actionId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<DonationDTO> getDonationsByStatus(DonationStatus status) {
        return donationRepository.findByStatus(status).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public BigDecimal getTotalDonationsForAction(Long charityActionId) {
        List<Donation> donations = donationRepository.findByActionId(charityActionId);
        return donations.stream()
                .filter(donation -> donation.getStatus() == DonationStatus.VALIDATED)
                .map(Donation::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public BigDecimal getTotalDonationsByUser(Long userId) {
        List<Donation> donations = donationRepository.findByUserId(userId);
        return donations.stream()
                .filter(donation -> donation.getStatus() == DonationStatus.VALIDATED)
                .map(Donation::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public void processPayment(DonationDTO donationDTO) {
        Donation donation = donationRepository.findById(donationDTO.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Don non trouvé avec l'id: " + donationDTO.getId()));
        donation.setStatus(DonationStatus.VALIDATED);
        donationRepository.save(donation);
    }

    @Override
    public void refundDonation(Long id) {
        Donation donation = donationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Don non trouvé avec l'id: " + id));
        donation.setStatus(DonationStatus.REJECTED);
        donationRepository.save(donation);
    }

    @Override
    public List<DonationDTO> getDonationsByOrganization(Long organizationId) {
        return donationRepository.findByActionOrganizationId(organizationId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public BigDecimal getTotalDonationsByOrganization(Long organizationId) {
        List<Donation> donations = donationRepository.findByActionOrganizationId(organizationId);
        return donations.stream()
                .filter(donation -> donation.getStatus() == DonationStatus.VALIDATED)
                .map(Donation::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public List<DonationDTO> getDonationsByOrganizationAndDateRange(Long organizationId, LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);
        
        return donationRepository.findByActionOrganizationId(organizationId).stream()
                .filter(donation -> {
                    LocalDateTime donationDate = donation.getDonationDate();
                    return donationDate != null && 
                           !donationDate.isBefore(startDateTime) && 
                           !donationDate.isAfter(endDateTime);
                })
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<DonationDTO> getRecentDonationsByOrganization(Long organizationId, int limit) {
        return donationRepository.findByActionOrganizationId(organizationId).stream()
                .sorted((d1, d2) -> d2.getDonationDate().compareTo(d1.getDonationDate()))
                .limit(limit)
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, BigDecimal> getMonthlyDonationsByOrganization(Long organizationId) {
        List<Donation> donations = donationRepository.findByOrganizationId(organizationId);
        Map<String, BigDecimal> monthlyDonations = new HashMap<>();

        for (Donation donation : donations) {
            YearMonth yearMonth = YearMonth.from(donation.getDonationDate());
            String key = yearMonth.toString();
            
            monthlyDonations.merge(key, donation.getAmount(), BigDecimal::add);
        }

        return monthlyDonations;
    }

    @Override
    public double getDonationsByOrganizationAndMonth(Organization organization, YearMonth yearMonth) {
        return donationRepository.sumAmountByOrganizationAndMonth(organization, 
            yearMonth.getYear(), 
            yearMonth.getMonthValue());
    }

    @Override
    public double getTotalDonationsByOrganization(Organization organization) {
        return donationRepository.sumAmountByOrganization(organization);
    }

    @Override
    public double getMonthlyDonationsByOrganization(Organization organization) {
        YearMonth currentMonth = YearMonth.now();
        return donationRepository.sumAmountByOrganizationAndMonth(organization, currentMonth.getYear(), currentMonth.getMonthValue());
    }

    @Override
    public double getAverageDonationsByOrganization(Organization organization) {
        List<Donation> donations = donationRepository.findByOrganizationId(organization.getId());
        if (donations.isEmpty()) {
            return 0.0;
        }
        return donations.stream()
                .filter(donation -> donation.getStatus() == DonationStatus.VALIDATED)
                .mapToDouble(donation -> donation.getAmount().doubleValue())
                .average()
                .orElse(0.0);
    }

    @Override
    public List<Donation> findRecentByOrganization(Organization organization, int limit) {
        return donationRepository.findTopByOrganizationOrderByDonationDateDesc(
            organization, 
            PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "donationDate"))
        );
    }

    @Override
    public List<DonationDTO> getDonationsByActionId(Long actionId) {
        return donationRepository.findByActionId(actionId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<DonationDTO> getDonationsByDonorId(Long donorId) {
        return donationRepository.findByUserId(donorId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void processDonation(String paymentId, String payerId, Long actionId, Double amount) {
        log.info("Début du traitement du don - Action ID: {}, Montant: {}€", actionId, amount);
        
        // 1. Récupérer l'action
        CharityAction action = charityActionRepository.findById(actionId)
                .orElseThrow(() -> new ResourceNotFoundException("Action non trouvée avec l'id: " + actionId));
        log.info("Action trouvée - Montant actuel: {}€", action.getCurrentAmount());

        // 2. Créer et sauvegarder le don
        Donation donation = new Donation();
        donation.setPaymentId(paymentId);
        donation.setPayerId(payerId);
        donation.setAmount(BigDecimal.valueOf(amount));
        donation.setAction(action);
        donation.setStatus(DonationStatus.VALIDATED);
        donation.setDonationDate(LocalDateTime.now());
        donation = donationRepository.save(donation);
        log.info("Don enregistré avec l'ID: {}", donation.getId());

        // 3. Mettre à jour le montant collecté de l'action
        BigDecimal currentAmount = action.getCurrentAmount() != null ? action.getCurrentAmount() : BigDecimal.ZERO;
        BigDecimal newAmount = currentAmount.add(BigDecimal.valueOf(amount));
        log.info("Mise à jour du montant - Ancien: {}€, Nouveau: {}€", currentAmount, newAmount);
        
        action.setCurrentAmount(newAmount);
        action.setUpdatedAt(LocalDateTime.now());
        action = charityActionRepository.save(action);
        
        // Vérification après sauvegarde
        log.info("Action mise à jour - Nouveau montant vérifié: {}€", action.getCurrentAmount());
        
        // Forcer le flush pour s'assurer que les changements sont persistés
        charityActionRepository.flush();
    }

    private void updateDonationFromDTO(Donation donation, DonationDTO dto) {
        donation.setAmount(dto.getAmount());
        donation.setDonorName(dto.getDonorName());
        donation.setDonationDate(dto.getDonationDate());
        if (dto.getStatus() != null) {
            donation.setStatus(dto.getStatus());
        }
    }

    private DonationDTO convertToDTO(Donation donation) {
        DonationDTO dto = new DonationDTO();
        dto.setId(donation.getId());
        dto.setDonorName(donation.getDonorName());
        dto.setAmount(donation.getAmount());
        dto.setStatus(donation.getStatus());
        dto.setDonationDate(donation.getDonationDate());
        if (donation.getAction() != null) {
            dto.setActionId(donation.getAction().getId());
            dto.setActionTitle(donation.getAction().getTitle());
            CharityActionDTO charityActionDTO = new CharityActionDTO();
            charityActionDTO.setId(donation.getAction().getId());
            charityActionDTO.setTitle(donation.getAction().getTitle());
            charityActionDTO.setDescription(donation.getAction().getDescription());
            charityActionDTO.setStartDate(donation.getAction().getStartDate());
            charityActionDTO.setEndDate(donation.getAction().getEndDate());
            charityActionDTO.setLocation(donation.getAction().getLocation());
            charityActionDTO.setTargetAmount(donation.getAction().getTargetAmount());
            charityActionDTO.setCurrentAmount(donation.getAction().getCurrentAmount());
            charityActionDTO.setCategory(donation.getAction().getCategory());
            charityActionDTO.setStatus(donation.getAction().getStatus());
            if (donation.getAction().getOrganization() != null) {
                charityActionDTO.setOrganizationId(donation.getAction().getOrganization().getId());
            }
            charityActionDTO.setMediaUrls(donation.getAction().getMediaUrls());
            charityActionDTO.setImageUrl(donation.getAction().getImageUrl());
            charityActionDTO.setCreatedAt(donation.getAction().getCreatedAt());
            charityActionDTO.setUpdatedAt(donation.getAction().getUpdatedAt());
            dto.setCharityAction(charityActionDTO);
        }
        if (donation.getOrganization() != null) {
            dto.setOrganizationId(donation.getOrganization().getId());
            dto.setOrganizationName(donation.getOrganization().getName());
        }
        return dto;
    }
}