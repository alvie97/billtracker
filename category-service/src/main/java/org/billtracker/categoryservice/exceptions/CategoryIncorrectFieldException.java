package org.billtracker.categoryservice.exceptions;

public class CategoryIncorrectFieldException extends RuntimeException {
    CategoryIncorrectFieldException(String field) {
        super("Incorrect field: " + field);
    }
}
