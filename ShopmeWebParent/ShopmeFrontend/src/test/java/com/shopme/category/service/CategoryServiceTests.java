package com.shopme.category.service;

import com.shopme.category.repository.CategoryRepository;
import com.shopme.common.entity.Category;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTests {
    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    void CategoryService_ListNoChildrenCategories_ReturnListOfCategories() {

        Category category1 = Category.builder()
                .id(1)
                .name("category1")
                .build();

        Category category2 = Category.builder()
                .id(2)
                .name("category2")
                .build();

        Category category3 = Category.builder()
                .id(3)
                .name("category3")
                .build();

        Category category4 = Category.builder()
                .id(4)
                .name("category4")
                .build();

        Category category5 = Category.builder()
                .id(5)
                .name("category5")
                .build();

        category1.setChildren(Set.of(category2));
        category2.setChildren(Set.of(category3));
        category4.setChildren(Set.of(category5));

        when(categoryRepository.findAllEnabled()).thenReturn(List.of(category1, category2, category3, category4, category5));

        List<Category> categories = categoryService.listNoChildrenCategories();
        for (Category category : categories) {
            System.out.println(category);
        }

        assertThat(categories).isNotNull();
    }
}
