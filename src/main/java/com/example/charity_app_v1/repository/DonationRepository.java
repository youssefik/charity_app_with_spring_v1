package com.example.charity_app_v1.repository;

import com.example.charity_app_v1.model.Donation;
import com.example.charity_app_v1.model.DonationStatus;
import com.example.charity_app_v1.model.Organization;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

@Repository
public interface DonationRepository extends JpaRepository<Donation, Long> {
    List<Donation> findByUserId(Long userId);
    List<Donation> findByActionId(Long actionId);
    List<Donation> findByStatus(DonationStatus status);
    List<Donation> findByDonorName(String donorName);

    @Query("SELECT d FROM Donation d WHERE d.action.organization.id = :organizationId")
    List<Donation> findByActionOrganizationId(@Param("organizationId") Long organizationId);

    @Query("SELECT SUM(d.amount) FROM Donation d WHERE d.action.id = :actionId AND d.status = 'VALIDATED'")
    BigDecimal getTotalDonationsForAction(@Param("actionId") Long actionId);

    @Query("SELECT SUM(d.amount) FROM Donation d WHERE d.user.id = ?1 AND d.status = 'VALIDATED'")
    BigDecimal getTotalDonationsByUser(Long userId);

    @Query("SELECT d FROM Donation d WHERE d.donationDate BETWEEN :startDate AND :endDate")
    List<Donation> findByDateBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT d FROM Donation d WHERE d.action.category.name = :categoryName AND d.donationDate BETWEEN :startDate AND :endDate")
    List<Donation> findByCategoryAndDateBetween(String categoryName, LocalDateTime startDate, LocalDateTime endDate);
    
    @Query("SELECT d FROM Donation d WHERE d.action.organization.name = :organizationName AND d.donationDate BETWEEN :startDate AND :endDate")
    List<Donation> findByOrganizationAndDateBetween(String organizationName, LocalDateTime startDate, LocalDateTime endDate);
    
    @Query("SELECT d FROM Donation d WHERE d.user.id = :userId ORDER BY d.amount DESC")
    List<Donation> findTopByUserId(Long userId);
    
    @Query("SELECT d FROM Donation d WHERE d.action.id = :actionId ORDER BY d.amount DESC")
    List<Donation> findTopByActionId(Long actionId);

    @Query("SELECT COUNT(d) FROM Donation d WHERE d.action.id = :actionId AND d.status = 'VALIDATED'")
    Long getDonationCountForAction(@Param("actionId") Long actionId);

    List<Donation> findByOrganizationId(Long organizationId);
    Page<Donation> findByOrganizationIdOrderByDonationDateDesc(Long organizationId, Pageable pageable);
    List<Donation> findByOrganizationIdAndStatus(Long organizationId, DonationStatus status);

    @Query("SELECT COALESCE(SUM(d.amount), 0) FROM Donation d WHERE d.organization = :organization AND YEAR(d.donationDate) = :year AND MONTH(d.donationDate) = :month")
    double sumAmountByOrganizationAndMonth(@Param("organization") Organization organization, 
                                         @Param("year") int year, 
                                         @Param("month") int month);

    @Query("SELECT COALESCE(SUM(d.amount), 0) FROM Donation d WHERE d.organization = :organization")
    double sumAmountByOrganization(@Param("organization") Organization organization);

    @Query("SELECT COALESCE(AVG(d.amount), 0) FROM Donation d WHERE d.organization = :organization")
    double averageAmountByOrganization(@Param("organization") Organization organization);

    @Query("SELECT d FROM Donation d WHERE d.organization = :organization ORDER BY d.donationDate DESC")
    List<Donation> findTopByOrganizationOrderByDonationDateDesc(@Param("organization") Organization organization, Pageable pageable);
}