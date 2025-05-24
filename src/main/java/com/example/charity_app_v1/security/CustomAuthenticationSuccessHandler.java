package com.example.charity_app_v1.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(CustomAuthenticationSuccessHandler.class);

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                      Authentication authentication) throws IOException, ServletException {
        try {
            String redirectUrl = "/home"; // URL par défaut
            
            logger.info("Authentification réussie pour l'utilisateur: {}", authentication.getName());
            logger.info("Rôles de l'utilisateur: {}", authentication.getAuthorities());

            if (authentication.getAuthorities() == null || authentication.getAuthorities().isEmpty()) {
                logger.warn("L'utilisateur n'a aucun rôle: {}", authentication.getName());
                redirectUrl = "/login?error=no_roles";
            } else {
                for (GrantedAuthority auth : authentication.getAuthorities()) {
                    String role = auth.getAuthority();
                    logger.info("Vérification du rôle: {}", role);
                    
                    if (role.equals("ROLE_ADMIN")) {
                        redirectUrl = "/admin/dashboard";
                        logger.info("Redirection vers le dashboard admin");
                        break;
                    } else if (role.equals("ROLE_ORGANIZATION")) {
                        redirectUrl = "/organization/dashboard";
                        logger.info("Redirection vers le dashboard organisation");
                        break;
                    } else if (role.equals("ROLE_USER")) {
                        redirectUrl = "/home";
                        logger.info("Redirection vers la page d'accueil");
                        break;
                    }
                }
            }

            logger.info("URL de redirection finale: {}", redirectUrl);
            response.sendRedirect(request.getContextPath() + redirectUrl);
        } catch (Exception e) {
            logger.error("Erreur lors de la redirection après authentification", e);
            response.sendRedirect(request.getContextPath() + "/login?error=redirect_failed");
        }
    }
} 