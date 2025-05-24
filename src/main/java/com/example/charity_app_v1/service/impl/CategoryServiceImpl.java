package com.example.charity_app_v1.service.impl;

import com.example.charity_app_v1.model.ActionCategory;
import com.example.charity_app_v1.repository.ActionCategoryRepository;
import com.example.charity_app_v1.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final ActionCategoryRepository actionCategoryRepository;

    @Autowired
    public CategoryServiceImpl(ActionCategoryRepository actionCategoryRepository) {
        this.actionCategoryRepository = actionCategoryRepository;
    }

    @Override
    public List<ActionCategory> getAllCategories() {
        return actionCategoryRepository.findAll();
    }

    @Override
    public Optional<ActionCategory> getCategoryById(Long id) {
        return actionCategoryRepository.findById(id);
    }

    @Override
    public Optional<ActionCategory> getCategoryByName(String name) {
        return actionCategoryRepository.findByName(name);
    }

    @Override
    public ActionCategory createCategory(String name, String description) {
        ActionCategory category = new ActionCategory();
        category.setName(name);
        category.setDescription(description);
        return actionCategoryRepository.save(category);
    }

    @Override
    public ActionCategory updateCategory(String name, String newName, String newDescription) {
        ActionCategory category = actionCategoryRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Category not found with name: " + name));
        category.setName(newName);
        category.setDescription(newDescription);
        return actionCategoryRepository.save(category);
    }

    @Override
    public void deleteCategory(String name) {
        ActionCategory category = actionCategoryRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Category not found with name: " + name));
        actionCategoryRepository.delete(category);
    }
} 