// src/main/java/com/example/charityapp/controller/HomeController.java

package com.example.charity_app_v1.controller;

import com.example.charity_app_v1.dto.CharityActionDTO;
import com.example.charity_app_v1.model.ActionCategory;
import com.example.charity_app_v1.service.CharityActionService;
import com.example.charity_app_v1.service.CategoryService;
import com.example.charity_app_v1.service.OrganizationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class HomeController {

    private final CharityActionService charityActionService;
    private final CategoryService categoryService;
    private final OrganizationService organizationService;

    @GetMapping("/dashboard")
    public String home(Model model,
                      @RequestParam(required = false) String category,
                      @RequestParam(required = false) String search) {
        try {
            // Récupérer les catégories pour le filtre
            model.addAttribute("categories", categoryService.getAllCategories());

            // Récupérer les actions caritatives
            List<CharityActionDTO> actions;
            if (category != null && !category.isEmpty()) {
                ActionCategory actionCategory = categoryService.getCategoryById(Long.parseLong(category))
                    .orElseThrow(() -> new RuntimeException("Category not found"));
                actions = charityActionService.getActionsByCategory(actionCategory.getId());
            } else if (search != null && !search.isEmpty()) {
                actions = charityActionService.searchActions(search);
            } else {
                actions = charityActionService.getActiveCharityActions();
            }
            model.addAttribute("actions", actions);

            // Statistiques
            model.addAttribute("totalActions", charityActionService.getTotalActiveActions());
            model.addAttribute("totalOrganizations", organizationService.getTotalActiveOrganizations());
            model.addAttribute("totalDonations", charityActionService.getTotalDonations());

            return "home";
        } catch (Exception e) {
            log.error("Erreur lors du chargement de la page d'accueil", e);
            model.addAttribute("error", "Une erreur est survenue lors du chargement de la page");
            return "home";
        }
    }
}