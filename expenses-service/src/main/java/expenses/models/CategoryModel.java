package expenses.models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CategoryModel {
    private String tag;
}
