package com.billtracker.backend.entities;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class CategoryRepositoryTest {

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    ExpenseRepository expenseRepository;

    @Test
    public void testFindExpensesById() {
        Category category = new Category("Groceries");
        Expense expense = new Expense("test1",
                                      "test desc" ,
                                      100.0,
                                      new Date());
        category.getExpenses().add(expense);
        categoryRepository.save(category);
        expenseRepository.save(expense);

        Set<Expense> expenses = expenseRepository.findExpensesByCategoryId(category.getId());
        assertEquals(expenses, category.getExpenses());
    }
}