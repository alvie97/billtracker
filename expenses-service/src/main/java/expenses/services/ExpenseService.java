package expenses.services;

import expenses.clients.CategoryClient;
import expenses.entities.Expense;
import expenses.models.CategoryModel;
import expenses.repositories.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ExpenseService {
    @Autowired
    ExpenseRepository expenseRepository;

    @Autowired
    CategoryClient categoryClient;

    public Expense findById(Long id) {
        return expenseRepository.findById(id)
                                .orElse(null);
    }

    public List<Expense> findAll() {
        return (List<Expense>) expenseRepository.findAll();
    }

    public Expense save(Expense expense) {
        return expenseRepository.save(expense);
    }

    public void delete(Long id) {
        expenseRepository.deleteById(id);
    }

    public List<CategoryModel> getExpenseCategories(Expense expense) {
        return expense.getCategoriesIds()
                                         .stream()
                                         .map(id -> categoryClient.getCategory(id).getContent())
                                         .collect(Collectors.toList());
    }

    public void addCategoryToExpenses(Long categoryId, List<Long> expensesIds) {
        expensesIds.forEach(id -> {
            Expense expense = findById(id);
            if (expense != null) {
                System.out.println(expense.getCategoriesIds().toString());
                save(expense);
            }
        });
    }
}
