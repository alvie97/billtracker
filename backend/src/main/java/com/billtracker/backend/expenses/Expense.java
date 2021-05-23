package com.billtracker.backend.expenses;

import com.billtracker.backend.categories.Category;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Relation(collectionRelation = "expenses", itemRelation = "expense")
public class Expense extends RepresentationModel<Expense> {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    @NotBlank
    private String name;

    @Size(max = 512)
    private String description;

    @DecimalMin(value = "0.0", inclusive = false)
    @NotNull
    private Double expense;

    private Instant date = Instant.now();

    @ManyToMany(mappedBy = "expenses")
    @JsonIgnore
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<Category> categories = new HashSet<>();
}
