package com.example.charity_app_v1.repository;

import com.example.charity_app_v1.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
 
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
} 