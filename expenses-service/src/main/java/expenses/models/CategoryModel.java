package expenses.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import org.springframework.hateoas.server.core.Relation;

@Data
@Builder
@Relation(collectionRelation = "categories")
public class CategoryModel {
    private Long id;

    private String tag;

    @JsonProperty("created_on")
    private String createdOn;
}
