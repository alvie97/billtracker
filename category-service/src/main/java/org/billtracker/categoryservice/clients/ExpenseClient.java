package org.billtracker.categoryservice.clients;

import org.billtracker.categoryservice.models.ExpenseModel;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "expenses-service", fallback = ExpenseFallback.class)
public
interface ExpenseClient {

    @GetMapping("/api/expenses/{id}/")
    EntityModel<ExpenseModel> getExpense(@PathVariable Long id);
}
