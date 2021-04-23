package com.billtracker.backend.entities;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@NoArgsConstructor
@Relation(collectionRelation = "expenses", itemRelation = "expense")
public class Expense extends RepresentationModel {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    private String name;

    private String description;

    private Double expense;

    private Date date;

    @ManyToMany(mappedBy = "expenses")
    private Set<Category> categories = new HashSet<>();

    public Expense(String name,
                   String description,
                   Double expense,
                   Date date) {
        this.name = name;
        this.description = description;
        this.expense = expense;
        this.date = date;
    }

}
