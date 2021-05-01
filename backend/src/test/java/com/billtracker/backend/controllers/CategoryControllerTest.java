package com.billtracker.backend.controllers;

import com.billtracker.backend.entities.Category;
import com.billtracker.backend.entities.Expense;
import com.billtracker.backend.services.CategoryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@WebMvcTest(CategoryController.class)
class CategoryControllerTest {

    final static String BASE_PATH = "http://localhost";
    @MockBean
    CategoryService categoryService;
    @Autowired
    private MockMvc mockMvc;
    private Category category;
    private List<Category> categories;
    private List<Expense> expenses;

    private static ObjectWriter ow;

    @BeforeAll
    public static void setup() {
        ow = new ObjectMapper().writer();
    }

    @BeforeEach
    public void init() {
        this.category = new Category("Groceries");
        this.category.setId(1L);
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

        for (int i = 0; i < this.categories.size(); ++i) {
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

    @Test
    public void getCategoryTest() throws Exception {
        when(categoryService.findById(this.category.getId()))
                .thenReturn(this.category);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/categories/1"))
               .andExpect(MockMvcResultMatchers.status().isOk())
               .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(this.category.getId()))
               .andExpect(MockMvcResultMatchers.jsonPath("$.tag").value(this.category.getTag()))
               .andExpect(MockMvcResultMatchers.jsonPath("$.created_on")
                                               .value(this.category.getCreatedOn().toString()))
               .andExpect(MockMvcResultMatchers.jsonPath("$.deleted_on").isEmpty());

        verify(categoryService, times(1)).findById(this.category.getId());
    }

    @Test
    public void addCategoryTest() throws Exception {
        when(categoryService.save(any(Category.class))).thenReturn(this.category);

        MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.post("/api/categories")
                                      .contentType(MediaType.APPLICATION_JSON)
                                      .accept(MediaType.APPLICATION_JSON)
                                      .characterEncoding("UTF-8")
                                      .content(String.format("{\"tag\": \"%s\"}", this.category.getTag()));

        mockMvc.perform(builder)
               .andExpect(MockMvcResultMatchers.status().isOk())
               .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(this.category.getId()))
               .andExpect(MockMvcResultMatchers.jsonPath("$.tag").value(this.category.getTag()))
               .andExpect(MockMvcResultMatchers.jsonPath("$.created_on")
                                               .value(this.category.getCreatedOn().toString()))
               .andExpect(MockMvcResultMatchers.jsonPath("$.deleted_on").isEmpty());

        verify(categoryService, times(1)).save(any(Category.class));
    }

    @Test
    public void updateCategoryTest() throws Exception {
        when(categoryService.save(any(Category.class))).thenReturn(null);

        MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.post("/api/categories/" + this.category.getId())
                                      .contentType(MediaType.APPLICATION_JSON)
                                      .accept(MediaType.APPLICATION_JSON)
                                      .characterEncoding("UTF-8")
                                      .content(String.format("{\"tag\": \"%s\"}", this.category.getTag()));

        mockMvc.perform(builder)
               .andExpect(MockMvcResultMatchers.status().isOk())
               .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(this.category.getId()))
               .andExpect(MockMvcResultMatchers.jsonPath("$.tag").value(this.category.getTag()))
               .andExpect(MockMvcResultMatchers.jsonPath("$.created_on")
                                               .value(this.category.getCreatedOn().toString()))
               .andExpect(MockMvcResultMatchers.jsonPath("$.deleted_on").isEmpty());

        verify(categoryService, times(1)).save(any(Category.class));
    }
}