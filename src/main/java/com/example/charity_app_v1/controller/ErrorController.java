package com.example.charity_app_v1.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/error")
public class ErrorController {

    @Value("${spring.profiles.active:prod}")
    private String activeProfile;

    @GetMapping
    public String handleError(
            @RequestParam(required = false) String message,
            HttpServletRequest request,
            Model model) {
        
        // Récupérer le message d'erreur du paramètre de requête
        if (message != null && !message.isEmpty()) {
            model.addAttribute("message", message);
        } else {
            // Si aucun message n'est fourni, utiliser un message par défaut
            model.addAttribute("message", "Une erreur inattendue s'est produite.");
        }

        // Ajouter l'URL de la requête pour le débogage
        model.addAttribute("requestUrl", request.getRequestURL());
        
        // Ajouter le code d'erreur HTTP
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        model.addAttribute("statusCode", statusCode != null ? statusCode : 500);

        // Déterminer si les détails de débogage doivent être affichés
        model.addAttribute("showDetails", "dev".equals(activeProfile));

        return "error";
    }
}