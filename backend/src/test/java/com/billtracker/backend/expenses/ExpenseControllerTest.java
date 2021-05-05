package com.billtracker.backend.expenses;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.Link;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@WebMvcTest(ExpenseController.class)
class ExpenseControllerTest {

    @MockBean
    private ExpenseService expenseService;

    @MockBean
    private ExpenseRepository expenseRepository;

    @Autowired
    private MockMvc mockMvc;

    private List<Expense> expenseList;

    private String API_BASE_PATH = "http://localhost";

    @BeforeEach
    void setup() {
        this.expenseList = new ArrayList<>();

        for (int i = 0; i < 10; ++i) {
            this.expenseList.add(new Expense(
                    "Test expense " + i,
                    "Test expense " + i + " desc",
                    i * 100 + 1.0));
            this.expenseList.get(i).setId((long) i);
        }
    }

    @Test
    void getAllExpensesTest() throws Exception {
        when(this.expenseService.findAll()).thenReturn(this.expenseList);

        ResultActions resultActions = this.mockMvc.perform(MockMvcRequestBuilders.get("/api/expenses"));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk())
                     .andExpect(MockMvcResultMatchers.jsonPath("$._embedded.expenses.size()")
                                                     .value(this.expenseList.size()));

        for (int i = 0; i < this.expenseList.size(); ++i) {
            resultActions.andExpect(MockMvcResultMatchers.jsonPath("$._embedded.expenses[" + i + "].id")
                                                         .value(this.expenseList.get(i).getId()));
            resultActions.andExpect(MockMvcResultMatchers.jsonPath("$._embedded.expenses[" + i + "].name")
                                                         .value(this.expenseList.get(i).getName()));
            resultActions.andExpect(MockMvcResultMatchers.jsonPath("$._embedded.expenses[" + i + "].description")
                                                         .value(this.expenseList.get(i).getDescription()));
            resultActions.andExpect(MockMvcResultMatchers.jsonPath("$._embedded.expenses[" + i + "].expense")
                                                         .value(this.expenseList.get(i).getExpense()));
            resultActions.andExpect(MockMvcResultMatchers.jsonPath("$._embedded.expenses[" + i + "].date")
                                                         .value(this.expenseList.get(i).getDate().toString()));

            Link link = linkTo(methodOn(ExpenseController.class).getExpense(this.expenseList.get(i)
                                                                                            .getId())).withSelfRel();
            resultActions.andExpect(MockMvcResultMatchers.jsonPath("$._embedded.expenses[" + i + "]._links.self.href")
                                                         .value(API_BASE_PATH + link.getHref()));
        }

        Link link = linkTo(methodOn(ExpenseController.class).getAllExpenses()).withSelfRel();

        resultActions.andExpect(MockMvcResultMatchers.jsonPath("$._links.self.href")
                                                     .value(API_BASE_PATH + link.getHref()));
    }

    @Test
    void getExpenseTest() throws Exception {
        Expense expense = this.expenseList.get(0);
        when(this.expenseService.findById(expense.getId())).thenReturn(expense);

        String selfLink = API_BASE_PATH + linkTo(methodOn(ExpenseController.class).getExpense(expense.getId()))
                                            .withSelfRel()
                                            .getHref();
        String expenseLink = API_BASE_PATH + linkTo(methodOn(ExpenseController.class).getAllExpenses())
                                                .withSelfRel()
                                                .getHref();

        this.mockMvc.perform(MockMvcRequestBuilders.get("/api/expenses/" + expense.getId()))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.id")
                                                    .value(expense.getId()))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.name")
                                                    .value(expense.getName()))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.description")
                                                    .value(expense.getDescription()))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.expense")
                                                    .value(expense.getExpense()))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.date")
                                                    .value(expense.getDate().toString()))
                    .andExpect(MockMvcResultMatchers.jsonPath("$._links.self.href").value(selfLink))
                    .andExpect(MockMvcResultMatchers.jsonPath("$._links.expenses.href").value(expenseLink));
    }

    @Test
    void getExpenseNotFoundTest() throws Exception {
        long notFoundId = 999;
        when(this.expenseService.findById(notFoundId)).thenReturn(null);

        String expenseLink = API_BASE_PATH + linkTo(methodOn(ExpenseController.class).getAllExpenses())
                .withSelfRel()
                .getHref();

        this.mockMvc.perform(MockMvcRequestBuilders.get("/api/expenses/" + notFoundId))
                    .andExpect(MockMvcResultMatchers.status().isNotFound())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                                                    .value("Expense " + notFoundId + " not found"))
                    .andExpect(MockMvcResultMatchers.jsonPath("$._links.expenses.href").value(expenseLink));
    }

    @Test
    void addExpenseTest() throws Exception {
        Expense expense = this.expenseList.get(0);
        when(this.expenseService.save(any(Expense.class))).thenReturn(expense);

        String selfLink = API_BASE_PATH + linkTo(methodOn(ExpenseController.class).getExpense(null))
                .withSelfRel()
                .getHref();

        String expenseLink = API_BASE_PATH + linkTo(methodOn(ExpenseController.class).getAllExpenses())
                .withSelfRel()
                .getHref();

        MockHttpServletRequestBuilder post_request =
                MockMvcRequestBuilders.post("/api/expenses")
                                      .contentType(MediaType.APPLICATION_JSON)
                                      .content(String.format("{\"name\": \"%s\", \"description\": \"%s\", \"expense\": %f}",
                                                             expense.getName(),
                                                             expense.getDescription(),
                                                             expense.getExpense()));
        this.mockMvc.perform(post_request)
                    .andExpect(MockMvcResultMatchers.status().isCreated())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.id")
                                                    .value(expense.getId()))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.name")
                                                    .value(expense.getName()))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.description")
                                                    .value(expense.getDescription()))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.expense")
                                                    .value(expense.getExpense()))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.date")
                                                    .value(expense.getDate().toString()))
                    .andExpect(MockMvcResultMatchers.jsonPath("$._links.self.href").value(selfLink))
                    .andExpect(MockMvcResultMatchers.jsonPath("$._links.expenses.href").value(expenseLink));
    }

    @Test
    void updateExpenseTest() throws Exception {
        Expense expense = this.expenseList.get(0);
        long expenseId = expense.getId();
        when(this.expenseService.findById(expenseId)).thenReturn(expense);

        String newExpenseDescription = "test description";
        double newExpenseExpense = 1337.0;

        String selfLink = API_BASE_PATH + linkTo(methodOn(ExpenseController.class).getExpense(expenseId))
                .withSelfRel()
                .getHref();

        String expenseLink = API_BASE_PATH + linkTo(methodOn(ExpenseController.class).getAllExpenses())
                .withSelfRel()
                .getHref();

        MockHttpServletRequestBuilder patch_request =
                MockMvcRequestBuilders.patch("/api/expenses/" + expenseId)
                                      .contentType(MediaType.APPLICATION_JSON)
                                      .content(String.format("{\"description\": \"%s\", \"expense\": %f}",
                                                             newExpenseDescription,
                                                             newExpenseExpense));

        this.mockMvc.perform(patch_request)
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.id")
                                                    .value(expense.getId()))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.name")
                                                    .value(expense.getName()))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.description")
                                                    .value(newExpenseDescription))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.expense")
                                                    .value(newExpenseExpense))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.date")
                                                    .value(expense.getDate().toString()))
                    .andExpect(MockMvcResultMatchers.jsonPath("$._links.self.href").value(selfLink))
                    .andExpect(MockMvcResultMatchers.jsonPath("$._links.expenses.href").value(expenseLink));
    }

    @Test
    void updateExpenseNotFoundTest() throws Exception {
        long notFoundId = 9999;
        when(this.expenseService.findById(notFoundId)).thenReturn(null);

        MockHttpServletRequestBuilder patch_request =
                MockMvcRequestBuilders.patch("/api/expenses/" + notFoundId)
                                      .contentType(MediaType.APPLICATION_JSON)
                                      .content("{}");

        String expenseLink = API_BASE_PATH + linkTo(methodOn(ExpenseController.class).getAllExpenses())
                .withSelfRel()
                .getHref();

        this.mockMvc.perform(patch_request)
                    .andExpect(MockMvcResultMatchers.status().isNotFound())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                                                    .value("Expense " + notFoundId + " not found"))
                    .andExpect(MockMvcResultMatchers.jsonPath("$._links.expenses.href").value(expenseLink));
    }

    @Test
    void updateExpenseIncorrectFieldTest() throws Exception {
        Expense expense = this.expenseList.get(0);
        long notFoundId = expense.getId();
        when(this.expenseService.findById(notFoundId)).thenReturn(expense);

        String newExpenseDescription = "test description";
        double newExpenseExpense = 1337.0;

        String selfLink = API_BASE_PATH + linkTo(methodOn(ExpenseController.class).getExpense(notFoundId))
                .withSelfRel()
                .getHref();

        String expenseLink = API_BASE_PATH + linkTo(methodOn(ExpenseController.class).getAllExpenses())
                .withSelfRel()
                .getHref();

        String field = "asdfasdf";

        MockHttpServletRequestBuilder patch_request =
                MockMvcRequestBuilders.patch("/api/expenses/" + notFoundId)
                                      .contentType(MediaType.APPLICATION_JSON)
                                      .content(String.format("{\"description\": \"%s\", \"%s\": %f}",
                                                             newExpenseDescription,
                                                             field,
                                                             newExpenseExpense));
        this.mockMvc.perform(patch_request)
                    .andExpect(MockMvcResultMatchers.status().isBadRequest())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                                                    .value("Incorrect field: " + field))
                    .andExpect(MockMvcResultMatchers.jsonPath("$._links.expenses.href").doesNotExist());
    }

    @Test
    void deleteExpenseTest() throws Exception {
       long expenseId = this.expenseList.get(0).getId();

       doNothing().when(this.expenseService).delete(expenseId);

        String expenseLink = API_BASE_PATH + linkTo(methodOn(ExpenseController.class).getAllExpenses())
                .withSelfRel()
                .getHref();

       this.mockMvc.perform(MockMvcRequestBuilders.delete("/api/expenses/" + expenseId))
                   .andExpect(MockMvcResultMatchers.status().isOk())
                   .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                                                   .value("Deleted expense with id "
                                                                  + expenseId
                                                                  + " successfully"))
                   .andExpect(MockMvcResultMatchers.jsonPath("$._links.expenses.href").value(expenseLink));

    }

    @Test
    void deleteExpenseNotFoundTest() throws Exception {
        long notFound = 9999;

        doNothing().when(this.expenseService).delete(notFound);

        String expenseLink = API_BASE_PATH + linkTo(methodOn(ExpenseController.class).getAllExpenses())
                .withSelfRel()
                .getHref();

        this.mockMvc.perform(MockMvcRequestBuilders.delete("/api/expenses/" + notFound))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                                                    .value("Deleted expense with id "
                                                                   + notFound
                                                                   + " successfully"))
                    .andExpect(MockMvcResultMatchers.jsonPath("$._links.expenses.href").value(expenseLink));
    }
}