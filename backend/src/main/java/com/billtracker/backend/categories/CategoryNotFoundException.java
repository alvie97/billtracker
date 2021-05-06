package com.billtracker.backend.categories;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class CategoryNotFoundException extends RuntimeException{
    CategoryNotFoundException(Long id) {
        super("Expense " + id + " not found");
    }
}
