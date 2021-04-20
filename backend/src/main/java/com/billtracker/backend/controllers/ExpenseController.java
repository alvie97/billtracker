package com.billtracker.backend.controllers;

import com.billtracker.backend.entities.Expense;
import com.billtracker.backend.services.ExpenseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api")
public class ExpenseController {

    private static final Logger log =
            LoggerFactory.getLogger(ExpenseController.class);

    @Autowired
    ExpenseService expenseService;

    @GetMapping("/expenses")
    public CollectionModel<Expense> getAllExpenses() {
        List<Expense> expenses = expenseService.findAll();
        expenses.stream()
                .map(expense -> expense.add(linkTo(methodOn(ExpenseController.class)
                                                           .getExpense(expense.getId()))
                                                    .withSelfRel())).collect(
                Collectors.toList());

        return CollectionModel.of(expenses,
                                  linkTo(methodOn(ExpenseController.class).getAllExpenses())
                                          .withSelfRel());
    }

    @GetMapping("/expenses/{id}")
    public Expense getExpense(@PathVariable Long id) {
        Expense expense = expenseService.findById(id);
        expense.add(linkTo(methodOn(ExpenseController.class).getExpense(expense.getId()))
                            .withSelfRel());
        return expense;
    }

    @PostMapping("/expenses")
    public Expense addExpense(@RequestBody Expense expense) {

        Expense newExpense = expenseService.save(expense);

        newExpense.add(linkTo(methodOn(ExpenseController.class).getExpense(expense.getId()))
                            .withSelfRel());

        return newExpense;
    }

    @PutMapping("/expenses/{id}")
    public Expense updateExpense(@RequestBody Expense expense, @PathVariable Long id) {
        expense.setId(id);
        expenseService.save(expense);

        expense.add(linkTo(methodOn(ExpenseController.class).getExpense(expense.getId()))
                            .withSelfRel());

        return expense;
    }

    @DeleteMapping("/expenses/{id}")
    public RepresentationModel<?> deleteExpense(@PathVariable Long id) {
        expenseService.delete(id);
        Map<String, String> res = new HashMap<>();
        res.put("message", "Deleted Id " + id);
        EntityModel<Map<String, String>> model = EntityModel.of(res);
        model.add(linkTo(methodOn(ExpenseController.class).getAllExpenses()).withRel("expenses"));
        return model;
    }
}
