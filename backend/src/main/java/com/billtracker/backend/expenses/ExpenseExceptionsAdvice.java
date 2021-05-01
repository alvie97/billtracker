package com.billtracker.backend.expenses;

import com.billtracker.backend.utils.SimpleResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ExpenseExceptionsAdvice {

    @ResponseBody
    @ExceptionHandler(ExpenseNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    SimpleResponse ExpenseNotFoundHandler(ExpenseNotFoundException exception) {
        return new SimpleResponse(exception.getMessage());
    }

    @ResponseBody
    @ExceptionHandler(ExpenseIncorrectFieldException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    SimpleResponse ExpenseIncorrectFieldHandler(ExpenseIncorrectFieldException exception) {
        return new SimpleResponse(exception.getMessage());
    }
}
