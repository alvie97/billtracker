package org.billtracker.categoryservice.models;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.Instant;

@Data
@Builder
@EqualsAndHashCode(callSuper = false)
public class ExpenseModel {
    private Long id;

    private String name;

    private String description;

    private Double expense;

    private Instant date;
}
