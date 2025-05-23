package com.example.charity_app_v1.controller;

import com.example.charity_app_v1.dto.ActionDTO;
import com.example.charity_app_v1.service.ActionService;
import com.example.charity_app_v1.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/actions")
@RequiredArgsConstructor
public class ActionController {

    private final ActionService actionService;
    private final CategoryService categoryService;

    @GetMapping
    public String listActions(Model model, @RequestParam(required = false) Long categoryId) {
        List<ActionDTO> actions = actionService.getAllActions();
        log.info("Actions récupérées (publique) : {}", actions.size());
        model.addAttribute("actions", actions);
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("selectedCategoryId", categoryId);
        return "actions/list";
    }

    @GetMapping("/{id}")
    public String showActionDetails(@PathVariable Long id, Model model) {
        log.info("Affichage des détails de l'action ID: {}", id);
        try {
            ActionDTO action = actionService.getActionById(id);
            log.info("Action récupérée pour id {} : {}", id, action);
            if (action == null) {
                log.error("Action non trouvée avec l'ID: {}", id);
                return "redirect:/actions?error=Action non trouvée";
            }
            
            model.addAttribute("action", action);
            model.addAttribute("categories", categoryService.getAllCategories());
            return "actions/details";
        } catch (Exception e) {
            log.error("Erreur lors de l'affichage des détails de l'action", e);
            return "redirect:/actions?error=" + e.getMessage();
        }
    }
} 