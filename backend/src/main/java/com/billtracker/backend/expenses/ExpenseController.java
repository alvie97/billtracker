// TODO: field validation
package com.billtracker.backend.expenses;

import com.billtracker.backend.utils.SimpleResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api")
public class ExpenseController {

    @Autowired
    ExpenseService expenseService;

    @GetMapping("/expenses")
    public CollectionModel<Expense> getAllExpenses() {
        List<Expense> expenses = expenseService.findAll();
        expenses.stream()
                .map(expense -> expense.add(linkTo(methodOn(ExpenseController.class)
                                       .getExpense(expense.getId())).withSelfRel()));

        return CollectionModel.of(expenses,
                                  linkTo(methodOn(ExpenseController.class).getAllExpenses()).withSelfRel());
    }

    @GetMapping("/expenses/{id}")
    public Expense getExpense(@PathVariable Long id) {
        Expense expense = expenseService.findById(id);
        if (expense == null) {
            throw new ExpenseNotFoundException(id);
        }
        expense.add(linkTo(methodOn(ExpenseController.class).getExpense(expense.getId()))
                            .withSelfRel());
        expense.add(linkTo(methodOn(ExpenseController.class).getAllExpenses()).withRel("expenses"));
        return expense;
    }

    @PostMapping("/expenses")
    public Expense addExpense(@RequestBody Expense expense) {

        Expense newExpense = expenseService.save(expense);

        newExpense.add(linkTo(methodOn(ExpenseController.class).getExpense(expense.getId()))
                            .withSelfRel());
        newExpense.add(linkTo(methodOn(ExpenseController.class).getAllExpenses()).withRel("expenses"));

        return newExpense;
    }

    @PatchMapping("/expenses/{id}")
    public Expense updateExpense(@RequestBody Map<String, Object> expense, @PathVariable Long id) {

        Expense expenseToUpdate = expenseService.findById(id);

        if (expenseToUpdate == null) {
            throw new ExpenseNotFoundException(id);
        }

        expense.forEach((field, value) -> {
            Field fieldToUpdate = ReflectionUtils.findField(Expense.class, field);
            if (fieldToUpdate == null) {
                throw new ExpenseIncorrectFieldException(field);
            }
            fieldToUpdate.setAccessible(true);
            ReflectionUtils.setField(fieldToUpdate, expenseToUpdate, value);
        });
        expenseService.save(expenseToUpdate);

        expenseToUpdate.add(linkTo(methodOn(ExpenseController.class).getExpense(expenseToUpdate.getId()))
                            .withSelfRel());
        expenseToUpdate.add(linkTo(methodOn(ExpenseController.class).getAllExpenses()).withRel("expenses"));

        return expenseToUpdate;
    }

    @DeleteMapping("/expenses/{id}")
    public RepresentationModel<EntityModel<SimpleResponse>> deleteExpense(@PathVariable Long id) {
        expenseService.delete(id);
        SimpleResponse response = new SimpleResponse("Deleted expense with id " + id + " successfully");
        EntityModel<SimpleResponse> model = EntityModel.of(response);
        model.add(linkTo(methodOn(ExpenseController.class).getAllExpenses()).withRel("expenses"));
        return model;
    }
}
