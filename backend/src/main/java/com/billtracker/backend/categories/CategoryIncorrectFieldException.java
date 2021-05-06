package com.billtracker.backend.categories;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class CategoryIncorrectFieldException extends RuntimeException{
    CategoryIncorrectFieldException(String field) {
        super("Incorrect field: " + field);
    }
}
