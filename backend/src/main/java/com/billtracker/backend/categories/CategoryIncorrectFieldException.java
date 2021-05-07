package com.billtracker.backend.categories;

public class CategoryIncorrectFieldException extends RuntimeException {
    CategoryIncorrectFieldException(String field) {
        super("Incorrect field: " + field);
    }
}
