package expenses;

import expenses.entities.Expense;
import expenses.repositories.ExpenseRepository;
import expenses.services.ExpenseService;
import lombok.Data;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

import java.time.Instant;
import java.util.List;
import java.util.function.Consumer;

@SpringBootApplication
@EnableFeignClients
public class ExpensesServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExpensesServiceApplication.class, args);
    }

    @Bean
    public CommandLineRunner run(ExpenseRepository repository) {
        return args -> {
            Expense expense = Expense.builder()
                                     .id(1L)
                                     .expense(500.0)
                                     .description("default")
                                     .name("test expense")
                                     .date(Instant.now())
                                     .build();
            expense.getCategoriesIds().add(1L);
            repository.save(expense);
        };
    }

    @Bean
    public Consumer<CategoryExpensesEvent> categoryExpensesAdded(ExpenseService expenseService) {
        return categoryExpensesEvent -> {
            System.out.println("expenses added: " + categoryExpensesEvent.toString());
            categoryExpensesEvent.getExpensesIds().forEach(id -> {
                Expense expense = expenseService.findById(id);
                if (expense != null) {
                    expense.getCategoriesIds().add(categoryExpensesEvent.categoryId);
                }
            });
        };
    }

    @Bean
    public Consumer<CategoryExpensesEvent> categoryExpensesRemoved(ExpenseService expenseService) {
        return categoryExpensesEvent -> {
            System.out.println("expenses removed: " + categoryExpensesEvent.toString());
            categoryExpensesEvent.getExpensesIds().forEach(id -> {
                Expense expense = expenseService.findById(id);
                if (expense != null) {
                    expense.getCategoriesIds().remove(categoryExpensesEvent.categoryId);
                }
            });
        };
    }

    @Data
    static
    class CategoryExpensesEvent {
        Long categoryId;
        List<Long> expensesIds;
    }
}
