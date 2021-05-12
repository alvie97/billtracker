package com.billtracker.backend.categories;

import com.billtracker.backend.expenses.Expense;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import javax.persistence.*;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@Relation(collectionRelation = "categories", itemRelation = "category")
public class Category extends RepresentationModel<Category> {

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

    @JoinTable(name = "category_expense",
            joinColumns = {@JoinColumn(name = "category_id")},
            inverseJoinColumns = {@JoinColumn(name = "expense_id")})
    @ManyToMany
    @JsonIgnore
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<Expense> expenses = new HashSet<>();

    public Category(String tag) {
        this.tag = tag;
    }
}
