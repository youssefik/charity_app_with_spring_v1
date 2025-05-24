package com.example.charity_app_v1.service.impl;

import com.example.charity_app_v1.model.ActionCategory;
import com.example.charity_app_v1.repository.ActionCategoryRepository;
import com.example.charity_app_v1.service.ActionCategoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ActionCategoryServiceImpl implements ActionCategoryService {

    private final ActionCategoryRepository actionCategoryRepository;

    public ActionCategoryServiceImpl(ActionCategoryRepository actionCategoryRepository) {
        this.actionCategoryRepository = actionCategoryRepository;
    }

    @Override
    public List<ActionCategory> getAllCategories() {
        return actionCategoryRepository.findAll();
    }

    @Override
    public ActionCategory getCategoryById(Long id) {
        return actionCategoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));
    }

    @Override
    public ActionCategory getCategoryByName(String name) {
        return actionCategoryRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Category not found with name: " + name));
    }

    @Override
    public ActionCategory createCategory(ActionCategory category) {
        return actionCategoryRepository.save(category);
    }

    @Override
    public ActionCategory updateCategory(Long id, ActionCategory category) {
        ActionCategory existingCategory = getCategoryById(id);
        existingCategory.setName(category.getName());
        existingCategory.setDescription(category.getDescription());
        return actionCategoryRepository.save(existingCategory);
    }

    @Override
    public void deleteCategory(Long id) {
        actionCategoryRepository.deleteById(id);
    }
} 