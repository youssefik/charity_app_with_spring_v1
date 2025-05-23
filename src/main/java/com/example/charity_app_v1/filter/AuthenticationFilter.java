// src/main/java/com/example/charity_app_v1/filter/AuthenticationFilter.java
package com.example.charity_app_v1.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.logging.Logger;

@Component
public class AuthenticationFilter implements Filter {

    private static final Logger logger = Logger.getLogger(AuthenticationFilter.class.getName());

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest  req  = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        String uri = req.getRequestURI();

        // 1️⃣ Si Spring Security a déjà authentifié l’utilisateur (form-login / session), on passe
        Authentication existingAuth = SecurityContextHolder.getContext().getAuthentication();
        if (existingAuth != null
                && existingAuth.isAuthenticated()
                && !(existingAuth instanceof AnonymousAuthenticationToken)) {
            chain.doFilter(request, response);
            return;
        }

        // 2️⃣ Sinon, pour les URIs publiques, on ne fait rien non plus
        if (!requiresAuthentication(uri)) {
            chain.doFilter(request, response);
            return;
        }

        // 3️⃣ Enfin on exige le token dans le header Authorization
        String authToken = req.getHeader("Authorization");
        if (authToken == null || authToken.isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().write("Missing authentication token");
            return;
        }
        if (!isValidToken(authToken)) {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            resp.getWriter().write("Invalid authentication token");
            return;
        }

        logger.info("Authentication Success for: " + req.getMethod() + " " + uri);
        chain.doFilter(request, response);
    }

    private boolean requiresAuthentication(String uri) {
        return !uri.startsWith("/login")
                && !uri.startsWith("/register")
                && !uri.startsWith("/css")
                && !uri.startsWith("/js");
    }

    private boolean isValidToken(String authToken) {
        return "validToken".equals(authToken);
    }
}
