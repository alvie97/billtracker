package com.billtracker.backend;

import com.billtracker.backend.categories.Category;
import com.billtracker.backend.categories.CategoryRepository;
import com.billtracker.backend.expenses.Expense;
import com.billtracker.backend.expenses.ExpenseRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class BackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }

    @Bean
    public CommandLineRunner demo(ExpenseRepository expenseRepository, CategoryRepository categoryRepository) {
        return (args) -> {

            Expense expense = expenseRepository.save(new Expense("Servicio Agua", "Agua", 500.00));
            expenseRepository.save(new Expense("Servicio Gas", "Gas", 500.00));
            expenseRepository.save(new Expense("Servicio Luz", "Luz", 500.00));
            expenseRepository.save(new Expense("Internet", "Fibertel te odio", 3000.00));
            expenseRepository.save(new Expense("Empanadas", "Ale traeme unas empanadas vale", 1000.00));

            Category category = new Category("Groceries");
            category.getExpenses().add(expense);
            categoryRepository.save(category);

            Category internet = new Category("Internet");
            internet.getExpenses().add(expense);
            categoryRepository.save(internet);

            Category empanadas = new Category("Empanadas");
            empanadas.getExpenses().add(expense);
            categoryRepository.save(empanadas);
        };
    }
}
