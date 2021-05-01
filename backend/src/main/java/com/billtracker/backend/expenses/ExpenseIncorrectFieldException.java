package com.billtracker.backend.expenses;

public class ExpenseIncorrectFieldException extends RuntimeException {
    ExpenseIncorrectFieldException(String field) {
        super("Incorrect field: " + field);
    }
}
