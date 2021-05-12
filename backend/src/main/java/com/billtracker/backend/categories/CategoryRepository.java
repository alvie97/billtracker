package com.billtracker.backend.categories;

import com.billtracker.backend.expenses.Expense;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends CrudRepository<Category, Long> {
    Optional<Expense> findCategoryExpenseById(long id);
}
