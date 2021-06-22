package org.billtracker.categoryservice;

import org.billtracker.categoryservice.entities.Category;
import org.billtracker.categoryservice.repositories.CategoryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

import java.time.Instant;

@SpringBootApplication
@EnableFeignClients
public class CategoryServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CategoryServiceApplication.class, args);
    }

    @Bean
    public CommandLineRunner run(CategoryRepository repository) {
        return args -> {
            Category category = Category.builder().id(1L).tag("test tag").createdDate(Instant.now()).build();
            category.getExpensesIds().add(1L);
            repository.save(category);
        };
    }

}
