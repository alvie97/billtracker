package expenses.controllers;

import expenses.advices.SimpleResponse;
import expenses.entities.Expense;
import expenses.exceptions.ExpenseNotFoundException;
import expenses.models.CategoryModel;
import expenses.services.ExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.validation.SmartValidator;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api")
public class ExpenseController {

    @Autowired
    ExpenseService expenseService;

    @Autowired
    SmartValidator validator;

    @GetMapping("/expenses")
    public CollectionModel<Expense> getAllExpenses() {
        List<Expense> expenses = expenseService.findAll();
        expenses.forEach(expense -> expense.add(linkTo(methodOn(ExpenseController.class)
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
        expense.add(linkTo(methodOn(ExpenseController.class).getExpenseCategories(expense.getId()))
                            .withRel("categories"));
        expense.add(linkTo(methodOn(ExpenseController.class).getAllExpenses()).withRel("expenses"));
        return expense;
    }

    @PostMapping("/expenses")
    @ResponseStatus(HttpStatus.CREATED)
    public Expense addExpense(@RequestBody @Valid Expense expense) {

        Expense newExpense = expenseService.save(expense);

        newExpense.add(linkTo(methodOn(ExpenseController.class).getExpense(expense.getId()))
                               .withSelfRel());
        newExpense.add(linkTo(methodOn(ExpenseController.class).getAllExpenses()).withRel("expenses"));

        return newExpense;
    }

    @PutMapping("/expenses/{id}")
    public Expense updateExpense(@RequestBody @Valid Expense expense,
                                 @PathVariable Long id) {

        if (expenseService.findById(id) == null) {
            throw new ExpenseNotFoundException(id);
        }

        expense.setId(id);
        Expense updatedExpense = expenseService.save(expense);

        updatedExpense.add(linkTo(methodOn(ExpenseController.class).getExpense(updatedExpense.getId()))
                                   .withSelfRel());
        updatedExpense.add(linkTo(methodOn(ExpenseController.class).getAllExpenses()).withRel("expenses"));

        return updatedExpense;
    }

    @DeleteMapping("/expenses/{id}")
    public SimpleResponse deleteExpense(@PathVariable Long id) {
        expenseService.delete(id);
        SimpleResponse response = new SimpleResponse("Deleted expense with id " + id + " successfully");
        response.add(linkTo(methodOn(ExpenseController.class).getAllExpenses()).withRel("expenses"));
        return response;
    }

    @GetMapping("/expenses/{id}/categories")
    public CollectionModel<CategoryModel> getExpenseCategories(@PathVariable long id) {

        Expense expense = expenseService.findById(id);

        if (expense == null) {
            throw new ExpenseNotFoundException(id);
        }

        List<CategoryModel> categories = expenseService.getExpenseCategories(expense);

        return CollectionModel.of(categories,
                                  linkTo(methodOn(ExpenseController.class).getExpenseCategories(id)).withSelfRel(),
                                  linkTo(methodOn(ExpenseController.class).getExpense(id)).withRel("expense"));
    }
}
