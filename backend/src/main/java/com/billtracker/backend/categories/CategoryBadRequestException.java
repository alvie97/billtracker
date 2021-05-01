package com.billtracker.backend.categories;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class CategoryBadRequestException extends RuntimeException{
    public CategoryBadRequestException(String message) {
        super(message);
    }
}
