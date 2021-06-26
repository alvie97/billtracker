package org.billtracker.categoryservice.controllers;

import org.billtracker.categoryservice.advices.SimpleResponse;
import org.billtracker.categoryservice.entities.Category;
import org.billtracker.categoryservice.exceptions.CategoryNotFoundException;
import org.billtracker.categoryservice.models.ExpenseModel;
import org.billtracker.categoryservice.services.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


@RestController
@RequestMapping("/api")
public class CategoryController {

    @Autowired
    CategoryService categoryService;

    @Autowired
    StreamBridge streamBridge;

    @GetMapping("/categories")
    public CollectionModel<Category> getAllCategories() {
        List<Category> categories = categoryService.findAll();
        categories = categories
                .stream()
                .map(category ->
                             category.add(linkTo(methodOn(CategoryController.class)
                                                         .getCategory(category.getId()))
                                                  .withSelfRel())
                                     .add(linkTo(methodOn(CategoryController.class).getCategoryExpenses(
                                             category.getId())).withRel("expenses")))
                .collect(Collectors.toList());
        return CollectionModel.of(categories,
                                  linkTo(methodOn(CategoryController.class).getAllCategories()).withSelfRel());
    }

    @GetMapping("/categories/{id}")
    public Category getCategory(@PathVariable Long id) {
        Category category = categoryService.findById(id);
        if (category == null) {
            throw new CategoryNotFoundException(id);
        }
        category.add(linkTo(methodOn(CategoryController.class).getCategory(category.getId())).withSelfRel())
                .add(linkTo(methodOn(CategoryController.class).getAllCategories()).withRel("categories"))
                .add(linkTo(methodOn(CategoryController.class).getCategoryExpenses(category.getId()))
                             .withRel("expenses"));
        return category;
    }

    @PostMapping("/categories")
    @ResponseStatus(HttpStatus.CREATED)
    public Category addCategory(@RequestBody @Valid Category category) {
        Category newCategory = categoryService.save(category);
        newCategory.add(linkTo(methodOn(CategoryController.class).getCategory(category.getId())).withSelfRel());
        newCategory.add(linkTo(methodOn(CategoryController.class).getAllCategories()).withRel("categories"));
        return newCategory;
    }

    @PutMapping("/categories/{id}")
    public Category updateCategory(@RequestBody @Valid Category category, @PathVariable Long id) {

        if (categoryService.findById(id) == null) {
            throw new CategoryNotFoundException(id);
        }

        category.setId(id);

        Category updatedCategory = categoryService.save(category);

        updatedCategory.add(linkTo(methodOn(CategoryController.class).getCategory(updatedCategory.getId()))
                                    .withSelfRel());
        updatedCategory.add(linkTo(methodOn(CategoryController.class).getAllCategories()).withRel("categories"));
        return updatedCategory;
    }

    @DeleteMapping("/categories/{id}")
    public SimpleResponse deleteCategory(@PathVariable Long id) {
        SimpleResponse simpleResponse;

        try {
            categoryService.delete(id);
        } catch (EmptyResultDataAccessException e) {
            throw new CategoryNotFoundException(id);
        }

        simpleResponse = new SimpleResponse("Deleted category with id " + id + " successfully");
        simpleResponse.add(linkTo(methodOn(CategoryController.class).getAllCategories()).withRel("categories"));
        return simpleResponse;
    }

    @GetMapping("/categories/{id}/expenses")
    public CollectionModel<ExpenseModel> getCategoryExpenses(@PathVariable Long id) {
        Category category = categoryService.findById(id);

        if (category == null) {
            throw new CategoryNotFoundException(id);
        }

        List<ExpenseModel> expenses = category.getExpensesIds()
                                              .stream()
                                              .map(expenseId -> categoryService.findCategoryExpenseById(expenseId))
                                              .collect(Collectors.toList());

        return CollectionModel.of(expenses,
                                  linkTo(methodOn(CategoryController.class).getCategoryExpenses(id)).withSelfRel(),
                                  linkTo(methodOn(CategoryController.class).getCategory(id)).withRel("category"));
    }

    @PostMapping("/categories/{id}/expenses")
    public SimpleResponse addExpenseToCategory(@PathVariable long id,
                                               @RequestBody @Valid CategoryExpensesRequest expensesIds) {
        Category category = categoryService.findById(id);

        if (category == null) {
            throw new CategoryNotFoundException(id);
        }

        category.getExpensesIds().addAll(expensesIds.getExpensesIds());

        categoryService.save(category);

        Map<String, Object> msg = new HashMap<>();
        msg.put("categoryId", category.getId());
        msg.put("expensesIds", expensesIds.getExpensesIds());

        streamBridge.send("categoryExpensesAdded-out-0", msg);

        SimpleResponse simpleResponse = new SimpleResponse("expenses added to category successfully");
        simpleResponse.add(linkTo(methodOn(CategoryController.class).getCategoryExpenses(id)).withRel("expenses"));

        return simpleResponse;
    }

    @DeleteMapping("/categories/{id}/expenses")
    public SimpleResponse removeExpenseFromCategory(@PathVariable long id,
                                                    @RequestBody @Valid CategoryExpensesRequest expensesIds) {
        Category category = categoryService.findById(id);

        if (category == null) {
            throw new CategoryNotFoundException(id);
        }

        expensesIds.getExpensesIds().forEach(category.getExpensesIds()::remove);
        categoryService.save(category);

        Map<String, Object> msg = new HashMap<>();
        msg.put("categoryId", category.getId());
        msg.put("expensesIds", expensesIds.getExpensesIds());

        streamBridge.send("categoryExpensesRemoved-out-0", msg);


        SimpleResponse simpleResponse = new SimpleResponse("expenses removed from category successfully");
        simpleResponse.add(linkTo(methodOn(CategoryController.class).getCategoryExpenses(id)).withRel("expenses"));

        return simpleResponse;
    }
}
