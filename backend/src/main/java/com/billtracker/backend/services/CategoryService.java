package com.billtracker.backend.services;

import com.billtracker.backend.entities.Category;
import com.billtracker.backend.entities.CategoryRepository;
import com.billtracker.backend.entities.Expense;
import com.billtracker.backend.entities.ExpenseRepository;
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

    public Set<Expense> findExpensesById(Long id) {
        return expenseRepository.findExpensesByCategoryId(id);
    }
}
