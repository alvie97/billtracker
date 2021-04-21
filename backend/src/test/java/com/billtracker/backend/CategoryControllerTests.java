package com.billtracker.backend;

import com.billtracker.backend.controllers.CategoryController;
import com.billtracker.backend.entities.Category;
import com.billtracker.backend.services.CategoryService;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.Link;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@WebMvcTest(CategoryController.class)
class CategoryControllerTests {
    final static Logger logger = LoggerFactory.getLogger(CategoryControllerTests.class);
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryService categoryService;

    final static String BASE_PATH = "http://localhost";

    @Test
    void testGetCategories() throws Exception {
        Category category = new Category("Groceries");
        category.setId(1L);
        when(this.categoryService.findAll()).thenReturn(List.of(category));

        String url = linkTo(methodOn(CategoryController.class).getAllCategories()).withSelfRel().getHref();

        logger.info(url);

        this.mockMvc
                .perform(MockMvcRequestBuilders.get(url))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$._embedded.size()")
                                                .value(1))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$._embedded.categoryList[0].id").value(1L))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$._embedded.categoryList[0].tag").value("Groceries"))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$._embedded.categoryList[0].createdOn").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$._embedded.categoryList[0].deletedOn").isEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$._embedded.categoryList[0]._links.self.href").value(BASE_PATH + url + "/" + 1))
                .andExpect(MockMvcResultMatchers.jsonPath(
                "$._links.self.href").value(BASE_PATH + url));
    }
}
