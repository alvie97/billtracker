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
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.lang.reflect.Field;
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
    public Category addCategory(@RequestBody Category category) {
        Category newCategory = categoryService.save(category);
        newCategory.add(linkTo(methodOn(CategoryController.class).getCategory(category.getId())).withSelfRel());
        newCategory.add(linkTo(methodOn(CategoryController.class).getAllCategories()).withRel("categories"));
        return newCategory;
    }

    @PatchMapping("/categories/{id}")
    public Category updateCategory(@RequestBody Map<String, Object> fields, @PathVariable Long id) {
        Category category = categoryService.findById(id);

        if (category == null) {
            throw new CategoryNotFoundException(id);
        }

        fields.forEach((k, v) -> {
            Field field = ReflectionUtils.findField(Category.class, k);
            if (field == null) {
                throw new CategoryIncorrectFieldException(k);
            }
            field.setAccessible(true);
            ReflectionUtils.setField(field, category, v);
        });

        categoryService.save(category);

        category.add(linkTo(methodOn(CategoryController.class).getCategory(category.getId()))
                             .withSelfRel());
        category.add(linkTo(methodOn(CategoryController.class).getAllCategories()).withRel("categories"));
        return category;
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
                                         ).collect(Collectors.toList());

        return CollectionModel.of(expenses,
                                  linkTo(methodOn(CategoryController.class).getCategoryExpenses(id)).withSelfRel(),
                                  linkTo(methodOn(CategoryController.class).getCategory(id)).withRel("category"),
                                  linkTo(methodOn(ExpenseController.class).getAllExpenses()).withRel("expenses"));
    }

    @PostMapping("/categories/{id}/expenses")
    public SimpleResponse addExpenseToCategory(@PathVariable long id,
                                               @Valid @RequestBody CategoryExpensesRequest expensesIds) {
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

                       category.getExpenses().add(expense);
                       expense.getCategories().add(category);
                   });

        categoryService.save(category);

        SimpleResponse simpleResponse = new SimpleResponse("expenses added to category successfully");
        simpleResponse.add(linkTo(methodOn(CategoryController.class).getCategoryExpenses(id)).withRel("expenses"));

        return simpleResponse;
    }

    @DeleteMapping("/categories/{id}/expenses")
    public SimpleResponse removeExpenseFromCategory(@PathVariable long id,
                                                    @RequestBody CategoryExpensesRequest expensesIds) {
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

                       category.getExpenses().remove(expense);
                       expense.getCategories().remove(category);
                   });


        categoryService.save(category);

        SimpleResponse simpleResponse = new SimpleResponse("expenses removed from category successfully");
        simpleResponse.add(linkTo(methodOn(CategoryController.class).getCategoryExpenses(id)).withRel("expenses"));

        return simpleResponse;
    }
}
