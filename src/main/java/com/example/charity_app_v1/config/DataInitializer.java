package com.example.charity_app_v1.config;

import com.example.charity_app_v1.model.Role;
import com.example.charity_app_v1.model.User;
import com.example.charity_app_v1.repository.RoleRepository;
import com.example.charity_app_v1.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        logger.info("Démarrage de l'initialisation des données...");
        
        // Initialiser les rôles s'ils n'existent pas
        initializeRoles();
        
        // Initialiser l'utilisateur admin s'il n'existe pas
        initializeAdminUser();
        
        logger.info("Initialisation des données terminée.");
    }

    @Transactional
    protected void initializeRoles() {
        logger.info("Initialisation des rôles...");
        
        // Rôle USER
        if (!roleRepository.existsByName("USER")) {
            Role userRole = new Role("USER");
            roleRepository.save(userRole);
            logger.info("Rôle USER créé");
        }

        // Rôle ORGANIZATION
        if (!roleRepository.existsByName("ORGANIZATION")) {
            Role orgRole = new Role("ORGANIZATION");
            roleRepository.save(orgRole);
            logger.info("Rôle ORGANIZATION créé");
        }

        // Rôle ADMIN
        if (!roleRepository.existsByName("ADMIN")) {
            Role adminRole = new Role("ADMIN");
            roleRepository.save(adminRole);
            logger.info("Rôle ADMIN créé");
        }
    }

    @Transactional
    protected void initializeAdminUser() {
        logger.info("Initialisation de l'utilisateur admin...");
        
        if (!userRepository.existsByEmail("superadmin@charity.com")) {
            User admin = new User();
            admin.setFirstName("Super");
            admin.setLastName("Admin");
            admin.setEmail("superadmin@charity.com");
            admin.setPassword(passwordEncoder.encode("superadmin123"));
            admin.setPhoneNumber("0600000000");
            admin.setEnabled(true);
            admin.setActive(true);
            
            // Récupérer le rôle ADMIN
            Role adminRole = roleRepository.findByName("ADMIN")
                    .orElseThrow(() -> new RuntimeException("Rôle ADMIN non trouvé"));
            
            // Assigner le rôle ADMIN à l'utilisateur
            Set<Role> roles = new HashSet<>();
            roles.add(adminRole);
            admin.setRoles(roles);
            
            try {
                userRepository.save(admin);
                logger.info("Utilisateur admin créé avec succès");
            } catch (Exception e) {
                logger.error("Erreur lors de la création de l'utilisateur admin: {}", e.getMessage());
                throw e;
            }
        } else {
            // Mettre à jour l'utilisateur admin existant
            User admin = userRepository.findByEmail("superadmin@charity.com")
                    .orElseThrow(() -> new RuntimeException("Utilisateur admin non trouvé"));
            
            // S'assurer que l'utilisateur a le rôle ADMIN
            Role adminRole = roleRepository.findByName("ADMIN")
                    .orElseThrow(() -> new RuntimeException("Rôle ADMIN non trouvé"));
            
            if (!admin.getRoles().contains(adminRole)) {
                admin.getRoles().add(adminRole);
                try {
                    userRepository.save(admin);
                    logger.info("Rôle ADMIN ajouté à l'utilisateur admin existant");
                } catch (Exception e) {
                    logger.error("Erreur lors de la mise à jour de l'utilisateur admin: {}", e.getMessage());
                    throw e;
                }
            }
        }
    }
} 