package com.example.charity_app_v1.repository;

import com.example.charity_app_v1.model.ActionCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ActionCategoryRepository extends JpaRepository<ActionCategory, Long> {
    Optional<ActionCategory> findByName(String name);
    void deleteByName(String name);
    boolean existsByName(String name);
} 