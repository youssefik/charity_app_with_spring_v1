package com.example.charity_app_v1.controller;

import com.example.charity_app_v1.dto.CharityActionDTO;
import com.example.charity_app_v1.model.ActionCategory;
import com.example.charity_app_v1.model.ActionStatus;
import com.example.charity_app_v1.service.CharityActionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/charity-actions")
@CrossOrigin(origins = "*")
public class CharityActionController {

    @Autowired
    private CharityActionService charityActionService;

    @PostMapping
    public ResponseEntity<CharityActionDTO> createAction(@RequestBody CharityActionDTO actionDTO) {
        return ResponseEntity.ok(charityActionService.createCharityAction(actionDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CharityActionDTO> updateAction(@PathVariable Long id, @RequestBody CharityActionDTO actionDTO) {
        return ResponseEntity.ok(charityActionService.updateCharityAction(id, actionDTO));
    }

    @PutMapping("/{id}/archive")
    public ResponseEntity<Void> archiveAction(@PathVariable Long id) {
        charityActionService.archiveAction(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CharityActionDTO> getActionById(@PathVariable Long id) {
        return ResponseEntity.ok(charityActionService.getActionById(id));
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<CharityActionDTO>> getActionsByCategory(@PathVariable Long categoryId) {
        return ResponseEntity.ok(charityActionService.getActionsByCategory(categoryId));
    }

    @GetMapping("/popular")
    public ResponseEntity<List<CharityActionDTO>> getPopularActions() {
        return ResponseEntity.ok(charityActionService.getPopularActions());
    }

    @GetMapping("/recommended")
    public ResponseEntity<List<CharityActionDTO>> getRecommendedActions() {
        return ResponseEntity.ok(charityActionService.getRecommendedActions());
    }

    @GetMapping("/organization/{organizationId}")
    public ResponseEntity<List<CharityActionDTO>> getActionsByOrganization(@PathVariable Long organizationId) {
        return ResponseEntity.ok(charityActionService.getActionsByOrganization(organizationId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCharityAction(@PathVariable Long id) {
        charityActionService.deleteCharityAction(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<CharityActionDTO>> getCharityActionsByStatus(@PathVariable ActionStatus status) {
        return ResponseEntity.ok(charityActionService.getCharityActionsByStatus(status));
    }

    @GetMapping("/active")
    public ResponseEntity<List<CharityActionDTO>> getActiveCharityActions() {
        return ResponseEntity.ok(charityActionService.getActiveCharityActions());
    }

    @GetMapping("/active/category/{category}")
    public ResponseEntity<List<CharityActionDTO>> getActiveCharityActionsByCategory(@PathVariable ActionCategory category) {
        return ResponseEntity.ok(charityActionService.getActiveCharityActionsByCategory(category));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Void> updateCharityActionStatus(
            @PathVariable Long id,
            @RequestParam ActionStatus status) {
        charityActionService.updateCharityActionStatus(id, status);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/media")
    public ResponseEntity<Void> addMediaToCharityAction(
            @PathVariable Long id,
            @RequestParam String mediaUrl) {
        charityActionService.addMediaToCharityAction(id, mediaUrl);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/media")
    public ResponseEntity<Void> removeMediaFromCharityAction(
            @PathVariable Long id,
            @RequestParam String mediaUrl) {
        charityActionService.removeMediaFromCharityAction(id, mediaUrl);
        return ResponseEntity.ok().build();
    }
}