package com.billtracker.backend.services;

import com.billtracker.backend.entities.Expense;
import com.billtracker.backend.entities.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExpenseService {
    @Autowired
    ExpenseRepository expenseRepository;

    public Expense findById(Long id) {
        return expenseRepository.findById(id).get();
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
}
