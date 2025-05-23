package com.example.charity_app_v1.controller;

import com.example.charity_app_v1.dto.UserDTO;
import com.example.charity_app_v1.model.User;
import com.example.charity_app_v1.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    private UserDTO userDTO;

    @BeforeEach
    void setUp() {
        userDTO = new UserDTO();
        userDTO.setFirstName("John");
        userDTO.setLastName("Doe");
        userDTO.setEmail("john.doe@example.com");
        userDTO.setPassword("password123");
        userDTO.setPhoneNumber("1234567890");
        userDTO.setAddress("123 Main St");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void registerUser_Success() throws Exception {
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value(userDTO.getEmail()))
                .andExpect(jsonPath("$.firstName").value(userDTO.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(userDTO.getLastName()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getUserById_Success() throws Exception {
        // Créer un utilisateur dans la base de données
        User user = new User();
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setEmail(userDTO.getEmail());
        user.setPassword("encodedPassword");
        user.setPhoneNumber(userDTO.getPhoneNumber());
        user.setAddress(userDTO.getAddress());
        user = userRepository.save(user);

        mockMvc.perform(get("/api/users/{id}", user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(user.getEmail()))
                .andExpect(jsonPath("$.firstName").value(user.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(user.getLastName()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getUserById_NotFound() throws Exception {
        mockMvc.perform(get("/api/users/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateUser_Success() throws Exception {
        // Créer un utilisateur dans la base de données
        User user = new User();
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setEmail(userDTO.getEmail());
        user.setPassword("encodedPassword");
        user.setPhoneNumber(userDTO.getPhoneNumber());
        user.setAddress(userDTO.getAddress());
        user = userRepository.save(user);

        // Mettre à jour les données
        userDTO.setFirstName("Jane");
        userDTO.setLastName("Smith");

        mockMvc.perform(put("/api/users/{id}", user.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Jane"))
                .andExpect(jsonPath("$.lastName").value("Smith"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void enableUser_Success() throws Exception {
        // Créer un utilisateur désactivé
        User user = new User();
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setEmail(userDTO.getEmail());
        user.setPassword("encodedPassword");
        user.setPhoneNumber(userDTO.getPhoneNumber());
        user.setAddress(userDTO.getAddress());
        user.setEnabled(false);
        user = userRepository.save(user);

        mockMvc.perform(put("/api/users/{id}/enable", user.getId()))
                .andExpect(status().isOk());

        // Vérifier que l'utilisateur est activé
        User updatedUser = userRepository.findById(user.getId()).orElseThrow();
        assertTrue(updatedUser.isEnabled());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void disableUser_Success() throws Exception {
        // Créer un utilisateur activé
        User user = new User();
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setEmail(userDTO.getEmail());
        user.setPassword("encodedPassword");
        user.setPhoneNumber(userDTO.getPhoneNumber());
        user.setAddress(userDTO.getAddress());
        user.setEnabled(true);
        user = userRepository.save(user);

        mockMvc.perform(put("/api/users/{id}/disable", user.getId()))
                .andExpect(status().isOk());

        // Vérifier que l'utilisateur est désactivé
        User updatedUser = userRepository.findById(user.getId()).orElseThrow();
        assertFalse(updatedUser.isEnabled());
    }
} 