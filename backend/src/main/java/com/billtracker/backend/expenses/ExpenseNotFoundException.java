package com.billtracker.backend.expenses;

public class ExpenseNotFoundException extends RuntimeException {
    public ExpenseNotFoundException(Long id) {
        super("Expense " + id + " not found");
    }
}
