package com.example.charity_app_v1.service;

import com.example.charity_app_v1.model.ActionCategory;
import java.util.List;

public interface ActionCategoryService {
    List<ActionCategory> getAllCategories();
    ActionCategory getCategoryById(Long id);
    ActionCategory getCategoryByName(String name);
    ActionCategory createCategory(ActionCategory category);
    ActionCategory updateCategory(Long id, ActionCategory category);
    void deleteCategory(Long id);
} 