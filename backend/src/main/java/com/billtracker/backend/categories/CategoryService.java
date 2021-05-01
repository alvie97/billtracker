package com.billtracker.backend.categories;

import com.billtracker.backend.categories.Category;
import com.billtracker.backend.categories.CategoryRepository;
import com.billtracker.backend.expenses.Expense;
import com.billtracker.backend.expenses.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
public class CategoryService {
    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    ExpenseRepository expenseRepository;

    public Category findById(Long id) {
        return categoryRepository.findById(id).orElse(null);
    }

    public List<Category> findAll() {
        return (List<Category>) categoryRepository.findAll();
    }

    public Category save(Category category) {
        return categoryRepository.save(category);
    }

    public void delete(Long id) {
        categoryRepository.deleteById(id);
    }

}
