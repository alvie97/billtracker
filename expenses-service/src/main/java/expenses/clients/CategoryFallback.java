package expenses.clients;

import expenses.models.CategoryModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Component;

@Component
public class CategoryFallback implements CategoryClient{
    @Override
    public EntityModel<CategoryModel> getCategory(Long id) {
        return EntityModel.of(CategoryModel.builder().tag("Default null tag").build());
    }
}
