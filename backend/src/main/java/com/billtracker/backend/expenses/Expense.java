package com.billtracker.backend.expenses;

import com.billtracker.backend.categories.Category;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import javax.persistence.*;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Relation(collectionRelation = "expenses", itemRelation = "expense")
public class Expense extends RepresentationModel<Expense> {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    private String name;

    private String description;

    private Double expense;

    private Instant date = Instant.now();

    @ManyToMany(mappedBy = "expenses")
    private Set<Category> categories = new HashSet<>();

    public Expense(String name, String description, double expense) {
        this.name = name;
        this.description = description;
        this.expense = expense;
    }
}
