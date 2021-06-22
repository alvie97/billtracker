package org.billtracker.categoryservice.exceptions;

public class CategoryNotFoundException extends RuntimeException {
    public CategoryNotFoundException(Long id) {
        super("Category " + id + " not found");
    }
}
