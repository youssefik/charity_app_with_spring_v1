package com.example.charity_app_v1.controller;

import com.example.charity_app_v1.dto.OrganizationDTO;
import com.example.charity_app_v1.model.OrganizationStatus;
import com.example.charity_app_v1.service.OrganizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@RestController
@RequestMapping("/api/organizations")
@RequiredArgsConstructor
public class OrganizationController {

    private final OrganizationService organizationService;

    @PostMapping
    public ResponseEntity<OrganizationDTO> createOrganization(@RequestBody OrganizationDTO organizationDTO) {
        return new ResponseEntity<>(organizationService.createOrganization(organizationDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrganizationDTO> updateOrganization(@PathVariable Long id, @RequestBody OrganizationDTO organizationDTO) {
        return ResponseEntity.ok(organizationService.updateOrganization(id, organizationDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrganization(@PathVariable Long id) {
        organizationService.deleteOrganization(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrganizationDTO> getOrganizationById(@PathVariable Long id) {
        try {
            OrganizationDTO organization = organizationService.getOrganizationById(id);
            return ResponseEntity.ok(organization);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<OrganizationDTO>> getAllOrganizations() {
        return ResponseEntity.ok(organizationService.getAllOrganizations());
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<OrganizationDTO>> getOrganizationsByStatus(@PathVariable OrganizationStatus status) {
        return ResponseEntity.ok(organizationService.getOrganizationsByStatus(status));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Void> updateOrganizationStatus(
            @PathVariable Long id,
            @RequestParam OrganizationStatus status) {
        organizationService.updateOrganizationStatus(id, status);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/admin/{adminId}")
    public ResponseEntity<List<OrganizationDTO>> getOrganizationsByAdminId(@PathVariable Long adminId) {
        return ResponseEntity.ok(organizationService.getOrganizationsByAdminId(adminId));
    }
}

