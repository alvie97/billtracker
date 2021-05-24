package com.billtracker.backend.categories;

import com.billtracker.backend.expenses.Expense;
import com.billtracker.backend.expenses.ExpenseController;
import com.billtracker.backend.expenses.ExpenseRepository;
import com.billtracker.backend.expenses.ExpenseService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.hateoas.Link;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@WebMvcTest(CategoryController.class)
class CategoryControllerTest {

    @MockBean
    private CategoryService categoryService;

    @MockBean
    private CategoryRepository categoryRepository;

    @MockBean
    private ExpenseService expenseService;

    @MockBean
    private ExpenseRepository expenseRepository;

    @Autowired
    private MockMvc mockMvc;

    private List<Category> categoryList;

    private final String API_BASE_PATH = "http://localhost";

    @BeforeEach
    void setup() {
        this.categoryList = new ArrayList<>();

        for (int i = 0; i < 10; ++i) {
            this.categoryList.add(Category.builder()
                                          .tag("Test category " + i)
                                          .build());
            this.categoryList.get(i)
                             .setId((long) i);
        }
    }

    @Test
    void getAllCategoriesTest() throws Exception {
        when(this.categoryService.findAll()).thenReturn(this.categoryList);

        ResultActions resultActions = this.mockMvc.perform(MockMvcRequestBuilders.get("/api/categories"));

        resultActions.andExpect(MockMvcResultMatchers.status()
                                                     .isOk())
                     .andExpect(MockMvcResultMatchers.jsonPath("$._embedded.categories.size()")
                                                     .value(this.categoryList.size()));

        for (int i = 0; i < this.categoryList.size(); ++i) {
            resultActions.andExpect(MockMvcResultMatchers.jsonPath("$._embedded.categories[" + i + "].id")
                                                         .value(this.categoryList.get(i)
                                                                                 .getId()));
            resultActions.andExpect(MockMvcResultMatchers.jsonPath("$._embedded.categories[" + i + "].tag")
                                                         .value(this.categoryList.get(i)
                                                                                 .getTag()));
            resultActions.andExpect(MockMvcResultMatchers.jsonPath("$._embedded.categories[" + i + "].created_on")
                                                         .value(this.categoryList.get(i)
                                                                                 .getCreatedOn()
                                                                                 .toString()));
            resultActions.andExpect(MockMvcResultMatchers.jsonPath("$._embedded.categories[" + i + "].deleted_on")
                                                         .isEmpty());

            Link link = linkTo(methodOn(CategoryController.class).getCategory(this.categoryList.get(i)
                                                                                               .getId())).withSelfRel();
            resultActions.andExpect(MockMvcResultMatchers.jsonPath("$._embedded.categories[" + i + "]._links.self.href")
                                                         .value(API_BASE_PATH + link.getHref()));
        }

        Link link = linkTo(methodOn(CategoryController.class).getAllCategories()).withSelfRel();

        resultActions.andExpect(MockMvcResultMatchers.jsonPath("$._links.self.href")
                                                     .value(API_BASE_PATH + link.getHref()));
    }

    @Test
    void getCategoryTest() throws Exception {
        Category category = this.categoryList.get(0);
        when(this.categoryService.findById(category.getId())).thenReturn(category);

        String selfLink = API_BASE_PATH + linkTo(methodOn(CategoryController.class).getCategory(category.getId()))
                .withSelfRel()
                .getHref();
        String categoryLink = API_BASE_PATH + linkTo(methodOn(CategoryController.class).getAllCategories())
                .withSelfRel()
                .getHref();

        this.mockMvc.perform(MockMvcRequestBuilders.get("/api/categories/" + category.getId()))
                    .andExpect(MockMvcResultMatchers.status()
                                                    .isOk())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.id")
                                                    .value(category.getId()))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.tag")
                                                    .value(category.getTag()))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.created_on")
                                                    .value(category.getCreatedOn()
                                                                   .toString()))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.deleted_on")
                                                    .isEmpty())
                    .andExpect(MockMvcResultMatchers.jsonPath("$._links.self.href")
                                                    .value(selfLink))
                    .andExpect(MockMvcResultMatchers.jsonPath("$._links.categories.href")
                                                    .value(categoryLink));
    }

    @Test
    void getCategoryNotFoundTest() throws Exception {
        long notFoundId = 999;
        when(this.categoryService.findById(notFoundId)).thenReturn(null);

        String categoryLink = API_BASE_PATH + linkTo(methodOn(CategoryController.class).getAllCategories())
                .withSelfRel()
                .getHref();

        this.mockMvc.perform(MockMvcRequestBuilders.get("/api/categories/" + notFoundId))
                    .andExpect(MockMvcResultMatchers.status()
                                                    .isNotFound())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                                                    .value("Category " + notFoundId + " not found"))
                    .andExpect(MockMvcResultMatchers.jsonPath("$._links.categories.href")
                                                    .value(categoryLink));
    }

    @Test
    void addCategoryTest() throws Exception {
        Category category = this.categoryList.get(0);
        when(this.categoryService.save(any(Category.class))).thenReturn(category);

        String selfLink = API_BASE_PATH + linkTo(methodOn(CategoryController.class).getCategory(null))
                .withSelfRel()
                .getHref();

        String categoryLink = API_BASE_PATH + linkTo(methodOn(CategoryController.class).getAllCategories())
                .withSelfRel()
                .getHref();

        MockHttpServletRequestBuilder post_request =
                MockMvcRequestBuilders.post("/api/categories")
                                      .contentType(MediaType.APPLICATION_JSON)
                                      .content(String.format("{\"tag\": \"%s\"}", category.getTag()));

        this.mockMvc.perform(post_request)
                    .andExpect(MockMvcResultMatchers.status()
                                                    .isCreated())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.id")
                                                    .value(category.getId()))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.tag")
                                                    .value(category.getTag()))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.created_on")
                                                    .value(category.getCreatedOn()
                                                                   .toString()))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.deleted_on")
                                                    .isEmpty())
                    .andExpect(MockMvcResultMatchers.jsonPath("$._links.self.href")
                                                    .value(selfLink))
                    .andExpect(MockMvcResultMatchers.jsonPath("$._links.categories.href")
                                                    .value(categoryLink));
    }

    @Test
    void updateCategoryTest() throws Exception {
        Category category = this.categoryList.get(0);
        long categoryId = category.getId();
        when(this.categoryService.findById(categoryId)).thenReturn(category);

        String newCategoryTag = "test description test update";

        String selfLink = API_BASE_PATH + linkTo(methodOn(CategoryController.class).getCategory(categoryId))
                .withSelfRel()
                .getHref();

        String categoryLink = API_BASE_PATH + linkTo(methodOn(CategoryController.class).getAllCategories())
                .withSelfRel()
                .getHref();

        MockHttpServletRequestBuilder patch_request =
                MockMvcRequestBuilders.patch("/api/categories/" + categoryId)
                                      .contentType(MediaType.APPLICATION_JSON)
                                      .content(String.format("{\"tag\": \"%s\"}", newCategoryTag));

        this.mockMvc.perform(patch_request)
                    .andExpect(MockMvcResultMatchers.status()
                                                    .isOk())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.id")
                                                    .value(category.getId()))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.tag")
                                                    .value(newCategoryTag))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.created_on")
                                                    .value(category.getCreatedOn()
                                                                   .toString()))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.deleted_on")
                                                    .isEmpty())
                    .andExpect(MockMvcResultMatchers.jsonPath("$._links.self.href")
                                                    .value(selfLink))
                    .andExpect(MockMvcResultMatchers.jsonPath("$._links.categories.href")
                                                    .value(categoryLink));
    }

    @Test
    void updateCategoryNotFoundTest() throws Exception {
        long notFoundId = 9999;
        when(this.categoryService.findById(notFoundId)).thenReturn(null);

        MockHttpServletRequestBuilder patch_request =
                MockMvcRequestBuilders.patch("/api/categories/" + notFoundId)
                                      .contentType(MediaType.APPLICATION_JSON)
                                      .content("{}");

        String categoryLink = API_BASE_PATH + linkTo(methodOn(CategoryController.class).getAllCategories())
                .withSelfRel()
                .getHref();

        this.mockMvc.perform(patch_request)
                    .andExpect(MockMvcResultMatchers.status()
                                                    .isNotFound())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                                                    .value("Category " + notFoundId + " not found"))
                    .andExpect(MockMvcResultMatchers.jsonPath("$._links.categories.href")
                                                    .value(categoryLink));
    }

    @Test
    void updateCategoryIncorrectFieldTest() throws Exception {
        Category category = this.categoryList.get(0);
        long id = category.getId();
        when(this.categoryService.findById(id)).thenReturn(category);

        String field = "asdfasdf";

        MockHttpServletRequestBuilder patch_request =
                MockMvcRequestBuilders.patch("/api/categories/" + id)
                                      .contentType(MediaType.APPLICATION_JSON)
                                      .content(String.format("{\"%s\": \"Test incorrect field\"}", field));
        this.mockMvc.perform(patch_request)
                    .andExpect(MockMvcResultMatchers.status()
                                                    .isBadRequest())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                                                    .value("Incorrect field: " + field))
                    .andExpect(MockMvcResultMatchers.jsonPath("$._links.categories.href")
                                                    .doesNotExist());
    }

    @Test
    void deleteCategoryTest() throws Exception {
        long categoryId = this.categoryList.get(0)
                                           .getId();

        doNothing().when(this.categoryService)
                   .delete(categoryId);

        String categoryLink = API_BASE_PATH + linkTo(methodOn(CategoryController.class).getAllCategories())
                .withSelfRel()
                .getHref();

        this.mockMvc.perform(MockMvcRequestBuilders.delete("/api/categories/" + categoryId))
                    .andExpect(MockMvcResultMatchers.status()
                                                    .isOk())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                                                    .value("Deleted category with id "
                                                           + categoryId
                                                           + " successfully"))
                    .andExpect(MockMvcResultMatchers.jsonPath("$._links.categories.href")
                                                    .value(categoryLink));

    }

    @Test
    void deleteCategoryNotFoundTest() throws Exception {
        long notFoundId = 9999;

        doThrow(EmptyResultDataAccessException.class).when(this.categoryService)
                                                     .delete(notFoundId);

        String categoryLink = API_BASE_PATH + linkTo(methodOn(CategoryController.class).getAllCategories())
                .withSelfRel()
                .getHref();

        this.mockMvc.perform(MockMvcRequestBuilders.delete("/api/categories/" + notFoundId))
                    .andExpect(MockMvcResultMatchers.status()
                                                    .isNotFound())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                                                    .value("Category " + notFoundId + " not found"))
                    .andExpect(MockMvcResultMatchers.jsonPath("$._links.categories.href")
                                                    .value(categoryLink));
    }

    @Test
    void getCategoryExpensesTest() throws Exception {
        Category category = this.categoryList.get(0);

        for (int i = 0; i < 3; i++) {
            Expense auxExpense = Expense.builder()
                                        .id((long) i)
                                        .name("test " + i)
                                        .description("test desc " + i)
                                        .expense((double) i)
                                        .build();
            category.getExpenses()
                    .add(auxExpense);
        }

        when(this.categoryService.findById(category.getId())).thenReturn(category);

        List<Expense> expenseList = new ArrayList<>(category.getExpenses());

        ResultActions resultActions =
                this.mockMvc.perform(MockMvcRequestBuilders.get("/api/categories/" + category.getId() + "/expenses"))
                            .andExpect(MockMvcResultMatchers.status()
                                                            .isOk());


        resultActions.andExpect(MockMvcResultMatchers.status()
                                                     .isOk())
                     .andExpect(MockMvcResultMatchers.jsonPath("$._embedded.expenses.size()")
                                                     .value(expenseList.size()));

        for (int i = 0; i < expenseList.size(); ++i) {

            Expense expense = expenseList.get(i);

            resultActions.andExpect(MockMvcResultMatchers.jsonPath("$._embedded.expenses[" + i + "].id")
                                                         .value(expense.getId()));
            resultActions.andExpect(MockMvcResultMatchers.jsonPath("$._embedded.expenses[" + i + "].name")
                                                         .value(expense.getName()));
            resultActions.andExpect(MockMvcResultMatchers.jsonPath("$._embedded.expenses[" + i + "].description")
                                                         .value(expense.getDescription()));
            resultActions.andExpect(MockMvcResultMatchers.jsonPath("$._embedded.expenses[" + i + "].expense")
                                                         .value(expense.getExpense()));
            resultActions.andExpect(MockMvcResultMatchers.jsonPath("$._embedded.expenses[" + i + "].date")
                                                         .value(expense.getDate()
                                                                       .toString()));

            Link link = linkTo(methodOn(ExpenseController.class).getExpense(expense.getId())).withSelfRel();
            resultActions.andExpect(MockMvcResultMatchers.jsonPath("$._embedded.expenses[" + i + "]._links.self.href")
                                                         .value(API_BASE_PATH + link.getHref()));
        }

        Link selfLink = linkTo(methodOn(CategoryController.class).getCategoryExpenses(category.getId())).withSelfRel();
        Link categoryLink = linkTo(methodOn(CategoryController.class).getCategory(category.getId())).withRel("category");
        Link expensesLink = linkTo(methodOn(ExpenseController.class).getAllExpenses()).withRel("expenses");

        resultActions.andExpect(MockMvcResultMatchers.jsonPath("$._links.self.href")
                                                     .value(API_BASE_PATH + selfLink.getHref()))
                     .andExpect(MockMvcResultMatchers.jsonPath("$._links.category.href")
                                                     .value(API_BASE_PATH + categoryLink.getHref()))
                     .andExpect(MockMvcResultMatchers.jsonPath("$._links.expenses.href")
                                                     .value(API_BASE_PATH + expensesLink.getHref()));
    }

    @Test
    void getCategoryExpensesCategoryNotFoundTest() throws Exception {
        long notFoundId = 9999;
        when(categoryService.findById(notFoundId)).thenReturn(null);

        this.mockMvc.perform(MockMvcRequestBuilders.get("/api/categories/" + notFoundId + "/expenses"))
                    .andExpect(MockMvcResultMatchers.status()
                                                    .isNotFound());
    }

    @Test
    void addExpenseToCategoryTest() throws Exception {
        Category category = this.categoryList.get(0);
        when(categoryService.findById(category.getId())).thenReturn(category);

        List<Expense> expenses = new ArrayList<>();

        for (long i = 0; i < 3; i++) {
            Expense expense = Expense.builder().id(i).name("test" + i).description("test desc " + i).expense((double) i).build();
            when(expenseService.findById(i)).thenReturn(expense);
            expenses.add(expense);
        }

        ObjectMapper objectMapper = new ObjectMapper();
        CategoryExpensesRequest categoryExpensesRequest =
                new CategoryExpensesRequest(expenses.stream()
                                                    .map(Expense::getId)
                                                    .collect(Collectors.toList()));
        String contentJson = objectMapper.writeValueAsString(categoryExpensesRequest);

        Link expensesLink = linkTo(methodOn(CategoryController.class).getCategoryExpenses(category.getId()))
                .withRel("expenses");

        this.mockMvc.perform(
                MockMvcRequestBuilders.post("/api/categories/" + category.getId() + "/expenses")
                                      .contentType(MediaType.APPLICATION_JSON)
                                      .content(contentJson))
                    .andExpect(MockMvcResultMatchers.status()
                                                    .isOk())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                                                    .value("expenses added to category successfully"))
                    .andExpect(MockMvcResultMatchers.jsonPath("$._links.expenses.href")
                                                    .value(API_BASE_PATH + expensesLink.getHref()));

        assertThat(category.getExpenses()).extracting(Expense::getName)
                                          .containsExactlyInAnyOrderElementsOf(expenses.stream()
                                                                                       .map(Expense::getName)
                                                                                       .collect(Collectors.toList()));
    }

    @Test
    void addExpenseToCategoryEmptyRequestTest() throws Exception {
        Category category = categoryList.get(0);
        when(categoryService.findById(category.getId())).thenReturn(category);

        this.mockMvc.perform(MockMvcRequestBuilders.post("/api/categories/" + category.getId() + "/expenses")
                                                   .contentType(MediaType.APPLICATION_JSON)
                                                   .content("{}"))
                    .andExpect(MockMvcResultMatchers.status()
                                                    .isBadRequest());
    }

    @Test
    void addExpenseToCategoryCategoryNotFoundTest() throws Exception {
        long notFoundId = 9999;
        when(categoryService.findById(notFoundId)).thenReturn(null);

        CategoryExpensesRequest categoryExpensesRequest = new CategoryExpensesRequest();
        categoryExpensesRequest.setExpensesIds(new ArrayList<>());
        ObjectMapper objectMapper = new ObjectMapper();

        String JsonContent = objectMapper.writeValueAsString(categoryExpensesRequest);

        this.mockMvc.perform(MockMvcRequestBuilders.post("/api/categories/" + notFoundId + "/expenses")
                                                   .contentType(MediaType.APPLICATION_JSON)
                                                   .content(JsonContent))
                    .andExpect(MockMvcResultMatchers.status()
                                                    .isNotFound());
    }

    @Test
    void removeExpenseFromCategoryTest() throws Exception {
        Category category = this.categoryList.get(0);
        when(categoryService.findById(category.getId())).thenReturn(category);

        for (long i = 0; i < 3; i++) {
            Expense expense = Expense.builder().id(i).name("test "+  i).description("test desc " + i).expense((double) i).build();
            when(expenseService.findById(i)).thenReturn(expense);
            category.getExpenses()
                    .add(expense);
        }

        ObjectMapper objectMapper = new ObjectMapper();
        CategoryExpensesRequest categoryExpensesRequest =
                new CategoryExpensesRequest(category.getExpenses()
                                                    .stream()
                                                    .map(Expense::getId)
                                                    .limit(2)
                                                    .collect(Collectors.toList()));
        String contentJson = objectMapper.writeValueAsString(categoryExpensesRequest);

        Link expensesLink = linkTo(methodOn(CategoryController.class).getCategoryExpenses(category.getId()))
                .withRel("expenses");

        this.mockMvc.perform(
                MockMvcRequestBuilders.delete("/api/categories/" + category.getId() + "/expenses")
                                      .contentType(MediaType.APPLICATION_JSON)
                                      .content(contentJson))
                    .andExpect(MockMvcResultMatchers.status()
                                                    .isOk())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                                                    .value("expenses removed from category successfully"))
                    .andExpect(MockMvcResultMatchers.jsonPath("$._links.expenses.href")
                                                    .value(API_BASE_PATH + expensesLink.getHref()));

        assertThat(category.getExpenses()
                           .size()).isEqualTo(1);
    }

}