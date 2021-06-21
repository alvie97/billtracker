package expenses.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import expenses.models.CategoryModel;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import javax.persistence.*;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@EqualsAndHashCode(callSuper = false)
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Relation(collectionRelation = "expenses", itemRelation = "expense")
public class Expense extends RepresentationModel<Expense> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank
    @Size(max = 64)
    private String name;

    @Size(max = 512)
    private String description;

    @DecimalMin(value = "0.0", inclusive = false)
    @NotNull
    private Double expense;

    @NotNull
    private Instant date;

    @ElementCollection
    @JsonIgnore
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @Builder.Default
    private Set<Long> categoriesIds = new HashSet<>();

    @Transient
    @JsonIgnore
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @Builder.Default
    private List<CategoryModel> categories = new ArrayList<>();
}
