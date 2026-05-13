package com.semicolon.oms.catalog.repository;

import com.semicolon.oms.catalog.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByStatus(String status);
    boolean existsBySlug(String slug);
}
