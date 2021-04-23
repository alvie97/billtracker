package com.billtracker.backend.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import javax.persistence.*;
import java.time.Instant;
import java.util.*;

@Entity
@Data
@NoArgsConstructor
@Relation(collectionRelation = "categories", itemRelation = "category")
public class Category extends RepresentationModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String tag;

    @Column(name = "created_on")
    @JsonProperty("created_on")
    private Instant createdOn = Instant.now();

    @Column(name = "deleted_on")
    @JsonProperty("deleted_on")
    private Instant deletedOn;

    @JoinTable(name="category_expense",
            joinColumns = { @JoinColumn(name="category_id")},
            inverseJoinColumns = { @JoinColumn(name="expense_id")})
    @ManyToMany
    private Set<Expense> expenses = new HashSet<>();

    public Category(String tag) {
        this.tag = tag;
    }
}
