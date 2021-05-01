package com.billtracker.backend.expenses;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.*;

@WebMvcTest(ExpenseController.class)
class ExpenseControllerTest {

    @MockBean
    ExpenseService expenseService;

    @Test
    void getAllExpensesTest() {

    }

    @Test
    void getExpenseTest() {

    }

    @Test
    void getExpenseNotFoundTest() {

    }

    @Test
    void addExpenseTest() {

    }

    @Test
    void updateExpenseTest() {

    }

    @Test
    void updateExpenseNotFoundTest() {

    }

    @Test
    void updateExpenseIncorrectFieldTest() {

    }

    @Test
    void deleteExpenseTest() {

    }

    @Test
    void deleteExpenseNotFoundTest() {

    }
}