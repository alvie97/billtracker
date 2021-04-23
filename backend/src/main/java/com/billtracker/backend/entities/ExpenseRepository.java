package com.billtracker.backend.entities;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface ExpenseRepository extends CrudRepository<Expense, Long> {
    @Query("from Expense e join fetch e.categories c where c.id = ?1")
    Set<Expense> findExpensesByCategoryId(Long id);
}
