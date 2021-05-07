package com.billtracker.backend.categories;

public class CategoryNotFoundException extends RuntimeException {
    CategoryNotFoundException(Long id) {
        super("Category " + id + " not found");
    }
}
