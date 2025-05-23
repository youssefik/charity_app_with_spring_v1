// src/main/java/com/example/charity_app_v1/service/CustomUserDetailsService.java
package com.example.charity_app_v1.service;

import com.example.charity_app_v1.model.User;
import com.example.charity_app_v1.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        logger.info("Tentative de chargement de l'utilisateur avec l'email: {}", email);
        
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.error("Utilisateur non trouvé avec l'email: {}", email);
                    return new UsernameNotFoundException("User not found: " + email);
                });
        
        logger.info("Utilisateur trouvé: {}", user.getEmail());
        
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        user.getRoles().forEach(role -> {
            String authority = "ROLE_" + role.getName();
            logger.info("Ajout de l'autorité: {}", authority);
            authorities.add(new SimpleGrantedAuthority(authority));
        });
        
        logger.info("Autorités finales: {}", authorities);
        
        return new org.springframework.security.core.userdetails.User(
            user.getEmail(),
            user.getPassword(),
            user.isEnabled(),
            true, true, true,
            authorities
        );
    }
}
