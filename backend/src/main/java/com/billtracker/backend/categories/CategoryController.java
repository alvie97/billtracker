package com.billtracker.backend.categories;

import com.billtracker.backend.expenses.ExpenseNotFoundException;
import com.billtracker.backend.utils.SimpleResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


@RestController
@RequestMapping("/api")
public class CategoryController {

    @Autowired
    CategoryService categoryService;

    @GetMapping("/categories")
    public CollectionModel<Category> getAllCategories() {
        List<Category> categories = categoryService.findAll();
        categories.forEach(category -> category.add(linkTo(methodOn(CategoryController.class)
                                                                   .getCategory(category.getId()))
                                                            .withSelfRel()));
        return CollectionModel.of(categories,
                                  linkTo(methodOn(CategoryController.class).getAllCategories()).withSelfRel());
    }

    @GetMapping("/categories/{id}")
    public Category getCategory(@PathVariable Long id) {
        Category category = categoryService.findById(id);
        if (category == null) {
            throw new CategoryNotFoundException(id);
        }
        category.add(linkTo(methodOn(CategoryController.class).getCategory(category.getId())).withSelfRel());
        category.add(linkTo(methodOn(CategoryController.class).getAllCategories()).withRel("categories"));
        return category;
    }

    @PostMapping("/categories")
    @ResponseStatus(HttpStatus.CREATED)
    public Category addCategory(@RequestBody Category category) {
        Category newCategory = categoryService.save(category);
        newCategory.add(linkTo(methodOn(CategoryController.class).getCategory(category.getId())).withSelfRel());
        newCategory.add(linkTo(methodOn(CategoryController.class).getAllCategories()).withRel("categories"));
        return newCategory;
    }

    @PatchMapping("/categories/{id}")
    public Category updateCategory(@RequestBody Map<String, Object> fields, @PathVariable Long id) {
        Category category = categoryService.findById(id);

        if (category == null) {
            throw new CategoryNotFoundException(id);
        }

        fields.forEach((k, v) -> {
            Field field = ReflectionUtils.findField(Category.class, k);
            if (field == null) {
                throw new CategoryIncorrectFieldException(k);
            }
            field.setAccessible(true);
            ReflectionUtils.setField(field, category, v);
        });

        categoryService.save(category);

        category.add(linkTo(methodOn(CategoryController.class).getCategory(category.getId()))
                             .withSelfRel());
        category.add(linkTo(methodOn(CategoryController.class).getAllCategories()).withRel("categories"));
        return category;
    }

    @DeleteMapping("/categories/{id}")
    public SimpleResponse deleteCategory(@PathVariable Long id) {
        SimpleResponse simpleResponse;

        try {
            categoryService.delete(id);
        } catch (EmptyResultDataAccessException e) {
            throw new CategoryNotFoundException(id);
        }

        simpleResponse = new SimpleResponse("Deleted category with id " + id + " successfully");
        simpleResponse.add(linkTo(methodOn(CategoryController.class).getAllCategories()).withRel("categories"));
        return simpleResponse;
    }
}
