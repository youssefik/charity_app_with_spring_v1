package com.example.charity_app_v1.controller;

import com.example.charity_app_v1.dto.DonationReportDTO;
import com.example.charity_app_v1.dto.OrganizationDTO;
import com.example.charity_app_v1.dto.UserDTO;
import com.example.charity_app_v1.model.ActionCategory;
import com.example.charity_app_v1.model.OrganizationStatus;
import com.example.charity_app_v1.model.Role;
import com.example.charity_app_v1.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ReportService reportService;

    @Autowired
    private RoleService roleService;

    // Gestion des utilisateurs
    @GetMapping("/users")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PutMapping("/users/{id}/enable")
    public ResponseEntity<Void> enableUser(@PathVariable Long id) {
        userService.enableUser(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/users/{id}/disable")
    public ResponseEntity<Void> disableUser(@PathVariable Long id) {
        userService.disableUser(id);
        return ResponseEntity.ok().build();
    }

    // Gestion des organisations
    @GetMapping("/organizations/pending")
    public ResponseEntity<List<OrganizationDTO>> getPendingOrganizations() {
        return ResponseEntity.ok(organizationService.getOrganizationsByStatus(OrganizationStatus.PENDING));
    }

    @PutMapping("/organizations/{id}/approve")
    public ResponseEntity<Void> approveOrganization(@PathVariable Long id) {
        organizationService.updateOrganizationStatus(id, OrganizationStatus.APPROVED);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/organizations/{id}/reject")
    public ResponseEntity<Void> rejectOrganization(@PathVariable Long id) {
        organizationService.updateOrganizationStatus(id, OrganizationStatus.REJECTED);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/organizations/{id}/suspend")
    public ResponseEntity<Void> suspendOrganization(@PathVariable Long id) {
        organizationService.updateOrganizationStatus(id, OrganizationStatus.SUSPENDED);
        return ResponseEntity.ok().build();
    }

    // Gestion des catégories
    @GetMapping("/categories")
    public ResponseEntity<List<ActionCategory>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    @PostMapping("/categories")
    public ResponseEntity<ActionCategory> createCategory(
            @RequestParam String name,
            @RequestParam String description) {
        return ResponseEntity.ok(categoryService.createCategory(name, description));
    }

    @PutMapping("/categories/{name}")
    public ResponseEntity<Void> updateCategory(
            @PathVariable String name,
            @RequestParam String newName,
            @RequestParam String newDescription) {
        categoryService.updateCategory(name, newName, newDescription);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/categories/{name}")
    public ResponseEntity<Void> deleteCategory(@PathVariable String name) {
        categoryService.deleteCategory(name);
        return ResponseEntity.ok().build();
    }

    // Rapports détaillés
    @GetMapping("/reports/donations")
    public ResponseEntity<DonationReportDTO> getDonationReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(reportService.getDonationReport(startDate, endDate));
    }

    @GetMapping("/reports/donations/by-category")
    public ResponseEntity<Map<String, BigDecimal>> getDonationsByCategory(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(reportService.getDonationsByCategory(startDate, endDate));
    }

    @GetMapping("/reports/donations/by-organization")
    public ResponseEntity<Map<String, BigDecimal>> getDonationsByOrganization(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(reportService.getDonationsByOrganization(startDate, endDate));
    }

    @GetMapping("/reports/top-donors")
    public ResponseEntity<List<DonationReportDTO>> getTopDonors(@RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(reportService.getTopDonors(limit));
    }

    @GetMapping("/reports/top-actions")
    public ResponseEntity<List<DonationReportDTO>> getTopCharityActions(@RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(reportService.getTopCharityActions(limit));
    }

    // Gestion des rôles
    @GetMapping("/roles")
    public ResponseEntity<List<Role>> getAllRoles() {
        return ResponseEntity.ok(roleService.getAllRoles());
    }

    @PostMapping("/roles")
    public ResponseEntity<Role> createRole(@RequestBody Role role) {
        return ResponseEntity.ok(roleService.createRole(role));
    }

    @PutMapping("/roles/{id}")
    public ResponseEntity<Role> updateRole(
            @PathVariable Long id,
            @RequestBody Role role) {
        return ResponseEntity.ok(roleService.updateRole(id, role));
    }

    @DeleteMapping("/roles/{id}")
    public ResponseEntity<Void> deleteRole(@PathVariable Long id) {
        roleService.deleteRole(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/users/{userId}/roles/{roleName}")
    public ResponseEntity<Void> assignRoleToUser(
            @PathVariable Long userId,
            @PathVariable String roleName) {
        roleService.assignRoleToUser(userId, roleName);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/users/{userId}/roles/{roleName}")
    public ResponseEntity<Void> removeRoleFromUser(
            @PathVariable Long userId,
            @PathVariable String roleName) {
        roleService.removeRoleFromUser(userId, roleName);
        return ResponseEntity.ok().build();
    }

    // Statistiques et rapports
    @GetMapping("/statistics/users/count")
    public ResponseEntity<Long> getTotalUsersCount() {
        return ResponseEntity.ok((long) userService.getAllUsers().size());
    }

    @GetMapping("/statistics/organizations/count")
    public ResponseEntity<Long> getTotalOrganizationsCount() {
        return ResponseEntity.ok((long) organizationService.getAllOrganizations().size());
    }

    @GetMapping("/statistics/organizations/status")
    public ResponseEntity<Long> getOrganizationsCountByStatus(@RequestParam OrganizationStatus status) {
        return ResponseEntity.ok((long) organizationService.getOrganizationsByStatus(status).size());
    }
}