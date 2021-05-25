package com.billtracker.backend.categories;

import com.billtracker.backend.expenses.Expense;
import com.billtracker.backend.expenses.ExpenseController;
import com.billtracker.backend.expenses.ExpenseNotFoundException;
import com.billtracker.backend.expenses.ExpenseService;
import com.billtracker.backend.utils.SimpleResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


@RestController
@RequestMapping("/api")
public class CategoryController {

    @Autowired
    CategoryService categoryService;

    @Autowired
    ExpenseService expenseService;

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
    public CollectionModel<Expense> getCategoryExpenses(@PathVariable Long id) {
        Category category = categoryService.findById(id);

        if (category == null) {
            throw new CategoryNotFoundException(id);
        }

        List<Expense> expenses = category.getExpenses()
                                         .stream()
                                         .map(expense ->
                                                      expense.add(linkTo(methodOn(ExpenseController.class).getExpense(
                                                              expense.getId())).withSelfRel())
                                         )
                                         .collect(Collectors.toList());

        return CollectionModel.of(expenses,
                                  linkTo(methodOn(CategoryController.class).getCategoryExpenses(id)).withSelfRel(),
                                  linkTo(methodOn(CategoryController.class).getCategory(id)).withRel("category"),
                                  linkTo(methodOn(ExpenseController.class).getAllExpenses()).withRel("expenses"));
    }

    @PostMapping("/categories/{id}/expenses")
    public SimpleResponse addExpenseToCategory(@PathVariable long id,
                                               @RequestBody @Valid CategoryExpensesRequest expensesIds) {
        Category category = categoryService.findById(id);

        if (category == null) {
            throw new CategoryNotFoundException(id);
        }

        expensesIds.getExpensesIds()
                   .forEach(expenseId -> {
                       Expense expense = expenseService.findById(expenseId);

                       if (expense == null) {
                           throw new ExpenseNotFoundException(expenseId);
                       }

                       category.getExpenses()
                               .add(expense);
                       expense.getCategories()
                              .add(category);
                   });

        categoryService.save(category);

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

        expensesIds.getExpensesIds()
                   .forEach(expenseId -> {
                       Expense expense = expenseService.findById(expenseId);

                       if (expense == null) {
                           throw new ExpenseNotFoundException(expenseId);
                       }

                       category.getExpenses()
                               .remove(expense);
                       expense.getCategories()
                              .remove(category);
                   });


        categoryService.save(category);

        SimpleResponse simpleResponse = new SimpleResponse("expenses removed from category successfully");
        simpleResponse.add(linkTo(methodOn(CategoryController.class).getCategoryExpenses(id)).withRel("expenses"));

        return simpleResponse;
    }
}
