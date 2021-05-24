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

            Expense expense =
                    expenseRepository.save(Expense.builder()
                                                  .name("Servicio Agua")
                                                  .description("Agua")
                                                  .expense(500.00)
                                                  .build());
            expenseRepository.save(Expense.builder()
                                          .name("Servicio Gas")
                                          .description("Gas")
                                          .expense(500.00)
                                          .build());
            expenseRepository.save(Expense.builder()
                                          .name("Servicio Luz")
                                          .description("Luz")
                                          .expense(500.00)
                                          .build());
            expenseRepository.save(Expense.builder()
                                          .name("Internet")
                                          .description("Fibertel te odio")
                                          .expense(3000.00)
                                          .build());
            expenseRepository.save(Expense.builder()
                                          .name("Empanadas")
                                          .description("Ale traeme unas empanadas vale")
                                          .expense(1000.00)
                                          .build());

            Category category = Category.builder()
                                        .tag("Groceries")
                                        .build();
            category.getExpenses()
                    .add(expense);
            categoryRepository.save(category);

            Category internet = Category.builder()
                                        .tag("Internet")
                                        .build();
            internet.getExpenses()
                    .add(expense);
            categoryRepository.save(internet);

            Category empanadas = Category.builder()
                                         .tag("Empanadas")
                                         .build();
            empanadas.getExpenses()
                     .add(expense);
            categoryRepository.save(empanadas);
        };
    }
}
