package expenses;

import expenses.entities.Expense;
import expenses.repositories.ExpenseRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

import java.time.Instant;

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

}
