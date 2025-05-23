package com.example.charity_app_v1.controller;

import com.example.charity_app_v1.dto.UserDTO;
import com.example.charity_app_v1.service.UserService;
import com.example.charity_app_v1.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.validation.Valid;
import com.example.charity_app_v1.model.User;
import com.example.charity_app_v1.model.Role;
import com.example.charity_app_v1.repository.UserRepository;
import com.example.charity_app_v1.repository.RoleRepository;
import java.util.Set;
import java.util.HashSet;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestParam;
import com.example.charity_app_v1.model.OrganizationStatus;
import com.example.charity_app_v1.service.OrganizationService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.validation.Valid;
import com.example.charity_app_v1.model.User;
import com.example.charity_app_v1.model.Role;
import com.example.charity_app_v1.repository.UserRepository;
import com.example.charity_app_v1.repository.RoleRepository;
import java.util.Set;
import java.util.HashSet;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestParam;
import com.example.charity_app_v1.model.OrganizationStatus;
import com.example.charity_app_v1.service.OrganizationService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

@Slf4j
@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final OrganizationService organizationService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @GetMapping("/")
    public String index() {
        return "redirect:/dashboard";
    }

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new UserDTO());
        return "auth/register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") @Valid UserDTO userDto,
                             BindingResult result,
                             RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "auth/register";
        }

        try {
            userService.registerUser(userDto);
            redirectAttributes.addFlashAttribute("success", "Inscription réussie ! Vous pouvez maintenant vous connecter.");
            return "redirect:/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de l'inscription : " + e.getMessage());
            return "redirect:/register";
        }
    }

    @GetMapping("/home")
    public String home(Model model) {
        long userCount = userRepository.count();
        model.addAttribute("userCount", userCount);
        return "home";
    }

    @GetMapping("/login-success")
    public String loginSuccess(Authentication authentication) {
        log.info("Redirection après connexion pour l'utilisateur: {}", authentication.getName());
        
        if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            log.info("Redirection vers le dashboard admin");
            return "redirect:/admin/dashboard";
        } else if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ORGANIZATION"))) {
            var currentUser = userService.getCurrentUser();
            var organizations = organizationService.getOrganizationsByAdminId(currentUser.getId());
            
            if (organizations.isEmpty()) {
                log.info("Redirection vers l'enregistrement de l'organisation");
                return "redirect:/organization/register";
            }
            
            var organization = organizations.get(0);
            log.info("Statut de l'organisation: {}", organization.getStatus());
            
            return switch (organization.getStatus()) {
                case PENDING -> "redirect:/organization/pending-approval";
                case REJECTED -> "redirect:/organization/rejected";
                case APPROVED -> "redirect:/organization/dashboard";
                default -> "redirect:/";
            };
        }
        
        log.info("Redirection vers la page d'accueil");
        return "redirect:/";
    }
}