package com.billtracker.backend.controllers;

import com.billtracker.backend.entities.Category;
import com.billtracker.backend.entities.Expense;
import com.billtracker.backend.services.CategoryService;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@WebMvcTest(CategoryController.class)
class CategoryControllerTest {

    @MockBean
    CategoryService categoryService;

    @Autowired
    private MockMvc mockMvc;

    private Category category;

    private List<Category> categories;

    private List<Expense> expenses;

    final static String BASE_PATH = "http://localhost";

     @BeforeEach
     public void setup() {
        this.category = new Category("Groceries");
        this.expenses = new ArrayList<>();
        this.categories = new ArrayList<>();
        this.expenses.addAll(List.of(
                new Expense("test 1", "test desc 1", 100.00, new Date()),
                new Expense("test 2", "test desc 2", 200.00, new Date()),
                new Expense("test 3", "test desc 3", 300.00, new Date()),
                new Expense("test 4", "test desc 4", 400.00, new Date()),
                new Expense("test 5", "test desc 5", 500.00, new Date())
        ));

        for (int i = 0; i < this.expenses.size(); ++i) {
            this.expenses.get(i).setId(i + 1L);
        }

        this.categories.addAll(List.of(
                new Category("Category 1"),
                new Category("Category 2"),
                new Category("Category 3"),
                new Category("Category 4"),
                new Category("Category 5"),
                new Category("Category 6")
        ));
         for (int i = 0; i < this.categories.size(); ++i) {
             this.categories.get(i).setId(i + 1L);
         }
    }

    @Test
    public void getAllCategoriesTest() throws Exception {
        when(categoryService.findAll()).thenReturn(this.categories);


        String url = linkTo(methodOn(CategoryController.class).getAllCategories()).withSelfRel().getHref();

        ResultActions perform = this.mockMvc
                .perform(MockMvcRequestBuilders.get(url))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$._embedded.categories.size()")
                                                .value(this.categories.size()));

        for(int i = 0; i < this.categories.size(); ++i) {
            Category currCategory = this.categories.get(i);
            perform.andExpect(MockMvcResultMatchers
                                       .jsonPath("$._embedded.categories[" + i + "].id")
                                       .value(currCategory.getId()))
                    .andExpect(MockMvcResultMatchers
                                       .jsonPath("$._embedded.categories[" + i + "].tag")
                                                    .value(currCategory.getTag()))
                    .andExpect(MockMvcResultMatchers
                                       .jsonPath("$._embedded.categories[" + i + "].created_on")
                                       .value(currCategory.getCreatedOn().toString()))
                    .andExpect(MockMvcResultMatchers
                                       .jsonPath("$._embedded.categories[" + i + "].deleted_on")
                                       .value(currCategory.getDeletedOn()))
                    .andExpect(MockMvcResultMatchers.jsonPath(
                            "$._embedded.categories[" + i + "]._links.self.href")
                                                    .value(BASE_PATH + url + "/" + currCategory.getId()));
        }

        perform.andExpect(MockMvcResultMatchers.jsonPath(
                        "$._links.self.href").value(BASE_PATH + url));

    }

}