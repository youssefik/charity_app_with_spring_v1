package com.example.charity_app_v1.service;

import com.example.charity_app_v1.dto.UserDTO;
import com.example.charity_app_v1.model.User;
import java.util.List;
import java.util.Optional;

public interface UserService {
    UserDTO registerUser(UserDTO userDTO);
    UserDTO updateUser(Long id, UserDTO userDTO);
    void deleteUser(Long id);
    Optional<UserDTO> getUserById(Long id);
    Optional<UserDTO> getUserByEmail(String email);
    List<UserDTO> getAllUsers();
    void enableUser(Long id);
    void disableUser(Long id);
    boolean existsByEmail(String email);
    User getCurrentUser();
    String getCurrentUsername();
    Long getCurrentUserId();
    User findById(Long id);
}