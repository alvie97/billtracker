package com.billtracker.backend.services;

import com.billtracker.backend.entities.Category;
import com.billtracker.backend.entities.CategoryRepository;
import com.billtracker.backend.entities.Expense;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Date;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
class CategoryServiceTest {

    @MockBean
    CategoryRepository categoryRepository;

    @Autowired
    CategoryService categoryService;

    @Test
    public void testFindByID() {
       Long id = 1L;
       Category category = new Category("Groceries");
       when(categoryRepository.findById(id)).thenReturn(java.util.Optional.of(
               category));

       Category retCategory = categoryService.findById(id);
       assertEquals(category, retCategory);
    }

    @Test
    public void testFindAll() {
        List<Category> categoryList =
                List.of(new Category("Groceries"),
                        new Category("Gas"),
                        new Category("Internet"));

        when(categoryRepository.findAll()).thenReturn(categoryList);
        List<Category> categoryList1 = categoryService.findAll();
        assertEquals(categoryList, categoryList1);
    }

    @Test
    public void testSave() {
        Long id = 1L;
        Category categoryToSave = new Category("Test");
        categoryToSave.setId(id);

        when(categoryRepository.save(categoryToSave)).thenReturn(categoryToSave);
        Category categoryRet = categoryService.save(categoryToSave);
        assertEquals(categoryRet, categoryToSave);
    }

    @Test
    public void testFindAllExpensesById() {
        Long id = 1L;
        Category category = new Category("Groceries");
        Set<Expense> expenses = Set.of(
                new Expense("Test1",
                        "TestDesc1",
                            100.0,
                            new Date()),
                new Expense("Test2",
                            "TestDesc2",
                            200.0,
                            new Date()),
                new Expense("Test3",
                            "TestDesc3",
                            300.0,
                            new Date()));
        category.setExpenses(expenses);
        when(categoryRepository.findById(id)).thenReturn(java.util.Optional.of(
                category));
        Set<Expense> expenses1 = categoryService.findExpensesById(id);
        assertEquals(expenses, expenses1);
    }
}