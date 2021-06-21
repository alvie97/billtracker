package expenses.clients;

import expenses.models.CategoryModel;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "category-service", fallback = CategoryFallback.class)
public interface CategoryClient {

    @GetMapping("/categories/{id}")
    EntityModel<CategoryModel> getCategory(@PathVariable Long id);
}
