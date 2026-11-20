package com.campus.lostfound.service;

import com.campus.lostfound.entity.Category;

import java.util.List;

public interface CategoryService {
    List<Category> getAllCategories();
    Category getCategoryById(Long id);
    Category saveCategory(Category category);
    void deleteCategory(Long id);
    long getTotalCategoriesCount();
}
