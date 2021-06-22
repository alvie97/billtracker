package org.billtracker.categoryservice.services;

import org.billtracker.categoryservice.clients.ExpenseClient;
import org.billtracker.categoryservice.entities.Category;
import org.billtracker.categoryservice.models.ExpenseModel;
import org.billtracker.categoryservice.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class CategoryService {
    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    ExpenseClient expenseClient;

    public Category findById(Long id) {
        return categoryRepository.findById(id)
                                 .orElse(null);
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

    public ExpenseModel findCategoryExpenseById(long id) {
        EntityModel<ExpenseModel> expense = expenseClient.getExpense(id);
        return expense.getContent();
    }

}
