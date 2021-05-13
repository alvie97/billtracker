package com.billtracker.backend.categories;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryExpensesRequest {

    @JsonProperty("expenses_ids")
    private List<Long> expensesIds;
}
