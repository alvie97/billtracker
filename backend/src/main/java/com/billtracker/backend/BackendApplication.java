package com.billtracker.backend;

import com.billtracker.backend.expenses.Expense;
import com.billtracker.backend.expenses.ExpenseRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.Instant;

@SpringBootApplication
public class BackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }

    @Bean
    public CommandLineRunner demo(ExpenseRepository repository) {
        return (args) -> {
            repository.save(new Expense("Servicio Agua", "Agua", 500.00));
            repository.save(new Expense("Servicio Gas", "Gas", 500.00));
            repository.save(new Expense("Servicio Luz", "Luz", 500.00));
            repository.save(new Expense("Internet", "Fibertel te odio", 3000.00));
            repository.save(new Expense("Empanadas", "Ale traeme unas empanadas vale", 1000.00));
        };
    }
}
