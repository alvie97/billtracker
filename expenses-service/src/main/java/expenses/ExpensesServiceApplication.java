package expenses;

import expenses.entities.Expense;
import expenses.repositories.ExpenseRepository;
import expenses.services.ExpenseService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;

import java.lang.reflect.Array;
import java.time.Instant;
import java.util.List;
import java.util.Map;
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
    public Consumer<CategoryEvent> categoryExpensesAdded(ExpenseService expenseService) {
        return categoryExpensesEvent -> {
            System.out.println("expenses added: " + categoryExpensesEvent.toString());
            try {
                expenseService.addCategoryToExpenses(categoryExpensesEvent.getCategoryId(),
                                                     categoryExpensesEvent.getExpensesIds());
            } catch (org.hibernate.LazyInitializationException e) {
               e.printStackTrace(); 
            }
        };
    }

}
@Data
class CategoryEvent {
    Long categoryId;
    List<Long> expensesIds;
}
