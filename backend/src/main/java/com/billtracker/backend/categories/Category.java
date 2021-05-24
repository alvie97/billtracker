package com.billtracker.backend.categories;

import com.billtracker.backend.expenses.Expense;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Relation(collectionRelation = "categories", itemRelation = "category")
public class Category extends RepresentationModel<Category> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank
    @Size(max = 64)
    private String tag;

    @Column(name = "created_on")
    @JsonProperty("created_on")
    @Builder.Default
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
    @Builder.Default
    private Set<Expense> expenses = new HashSet<>();
}
