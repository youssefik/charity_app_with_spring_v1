package com.example.charity_app_v1.service;

import com.example.charity_app_v1.model.ActionCategory;
import java.util.List;
import java.util.Optional;

public interface CategoryService {
    List<ActionCategory> getAllCategories();
    Optional<ActionCategory> getCategoryById(Long id);
    Optional<ActionCategory> getCategoryByName(String name);
    ActionCategory createCategory(String name, String description);
    ActionCategory updateCategory(String name, String newName, String newDescription);
    void deleteCategory(String name);
} 