package expenses.exceptions;

public class ExpenseIncorrectFieldException extends RuntimeException {
    ExpenseIncorrectFieldException(String field) {
        super("Incorrect field: " + field);
    }
}
