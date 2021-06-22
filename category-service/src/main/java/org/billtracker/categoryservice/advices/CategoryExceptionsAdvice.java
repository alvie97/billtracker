package org.billtracker.categoryservice.advices;

import org.billtracker.categoryservice.controllers.CategoryController;
import org.billtracker.categoryservice.exceptions.CategoryIncorrectFieldException;
import org.billtracker.categoryservice.exceptions.CategoryNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@ControllerAdvice
public class CategoryExceptionsAdvice {

    @ResponseBody
    @ExceptionHandler(CategoryNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    SimpleResponse CategoryNotFoundHandler(CategoryNotFoundException exception) {
        SimpleResponse simpleResponse = new SimpleResponse(exception.getMessage());
        simpleResponse.add(linkTo(methodOn(CategoryController.class).getAllCategories()).withRel("categories"));
        return simpleResponse;
    }

    @ResponseBody
    @ExceptionHandler(CategoryIncorrectFieldException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    SimpleResponse CategoryIncorrectFieldHandler(CategoryIncorrectFieldException exception) {
        return new SimpleResponse(exception.getMessage());
    }
}
