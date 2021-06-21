package expenses.advices;

import expenses.controllers.ExpenseController;
import expenses.exceptions.ExpenseIncorrectFieldException;
import expenses.exceptions.ExpenseNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@ControllerAdvice
public class ExpenseExceptionsAdvice {

    @ResponseBody
    @ExceptionHandler(ExpenseNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    SimpleResponse ExpenseNotFoundHandler(ExpenseNotFoundException exception) {
        SimpleResponse simpleResponse = new SimpleResponse(exception.getMessage());
        simpleResponse.add(linkTo(methodOn(ExpenseController.class).getAllExpenses()).withRel("expenses"));
        return simpleResponse;
    }

    @ResponseBody
    @ExceptionHandler(ExpenseIncorrectFieldException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    SimpleResponse ExpenseIncorrectFieldHandler(ExpenseIncorrectFieldException exception) {
        return new SimpleResponse(exception.getMessage());
    }
}
