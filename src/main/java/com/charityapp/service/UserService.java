package com.charityapp.service;

import com.charityapp.dto.UserDTO;
import com.charityapp.model.User;

public interface UserService {
    User getCurrentUser();
    UserDTO getCurrentUserDTO();
    UserDTO getUserById(Long id);
    UserDTO getUserByEmail(String email);
    UserDTO createUser(UserDTO userDTO);
    UserDTO updateUser(Long id, UserDTO userDTO);
    void deleteUser(Long id);
    boolean existsByEmail(String email);
} 