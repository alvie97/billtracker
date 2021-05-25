package com.billtracker.backend.categories;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
        Category category = Category.builder().tag("Groceries").build();
        when(categoryRepository.findById(id)).thenReturn(java.util.Optional.of(
                category));

        Category retCategory = categoryService.findById(id);
        assertEquals(category, retCategory);
    }

    @Test
    public void testFindAll() {
        List<Category> categoryList =
                List.of(Category.builder().tag("Groceries").build(),
                        Category.builder().tag("Gas").build(),
                        Category.builder().tag("Internet").build());

        when(categoryRepository.findAll()).thenReturn(categoryList);
        List<Category> categoryList1 = categoryService.findAll();
        assertEquals(categoryList, categoryList1);
    }

    @Test
    public void testSave() {
        Long id = 1L;
        Category categoryToSave = Category.builder().tag("Test").build();
        categoryToSave.setId(id);

        when(categoryRepository.save(categoryToSave)).thenReturn(categoryToSave);
        Category categoryRet = categoryService.save(categoryToSave);
        assertEquals(categoryRet, categoryToSave);
    }
}