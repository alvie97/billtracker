package com.billtracker.backend.controllers;

import com.billtracker.backend.entities.Category;
import com.billtracker.backend.services.CategoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


@RestController
@RequestMapping("/api")
public class CategoryController {

    private static final Logger log =
            LoggerFactory.getLogger(CategoryController.class);

    @Autowired
    CategoryService categoryService;

    @GetMapping("/categories")
    public CollectionModel<Category> getAllCategories() {
        List<Category> categories = categoryService.findAll();
        categories.stream()
                .map(category -> category.add(linkTo(methodOn(CategoryController.class)
                                                           .getCategory(category.getId()))
                                                    .withSelfRel())).collect(
                Collectors.toSet());

        log.info(categories.toString());

        return CollectionModel.of(categories,
                                  linkTo(methodOn(CategoryController.class).getAllCategories())
                                          .withSelfRel());
    }

    @GetMapping("/categories/{id}")
    public Category getCategory(@PathVariable Long id) {
        Category category = categoryService.findById(id);
        category.add(linkTo(methodOn(CategoryController.class).getCategory(category.getId()))
                            .withSelfRel());
        return category;
    }

    @PostMapping("/categories")
    public Category addCategory(@RequestBody Category category) {

        Category newCategory = categoryService.save(category);

        newCategory.add(linkTo(methodOn(CategoryController.class).getCategory(category.getId()))
                               .withSelfRel());

        return newCategory;
    }

    @PatchMapping("/categories/{id}")
    public Category updateCategory(@RequestBody Map<String, Object> fields, @PathVariable Long id) {
        Category category = categoryService.findById(id);

        if (category == null) {
            throw new CategoryNotFoundException();
        }

        fields.forEach((k, v) -> {
            Field field = ReflectionUtils.findField(Category.class, k);
            if (field == null) {
                throw new CategoryBadRequestException("field " + k + " not Found");
            }
            field.setAccessible(true);
            ReflectionUtils.setField(field, category, v);
        });

        categoryService.save(category);

        category.add(linkTo(methodOn(CategoryController.class).getCategory(category.getId()))
                            .withSelfRel());

        return category;
    }

    @DeleteMapping("/categories/{id}")
    public RepresentationModel<?> deleteCategory(@PathVariable Long id) {
        categoryService.delete(id);
        Map<String, String> res = new HashMap<>();
        res.put("message", "Deleted Id " + id);
        EntityModel<Map<String, String>> model = EntityModel.of(res);
        model.add(linkTo(methodOn(CategoryController.class).getAllCategories()).withRel("categories"));
        return model;
    }
}
