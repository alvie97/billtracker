package org.billtracker.categoryservice.clients;

import org.billtracker.categoryservice.models.ExpenseModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class ExpenseFallback implements ExpenseClient {
    @Override
    public EntityModel<ExpenseModel> getExpense(Long id) {

        return EntityModel.of(ExpenseModel.builder()
                                          .name("expense default")
                                          .description("expense default")
                                          .date(Instant.now())
                                          .build());
    }
}
