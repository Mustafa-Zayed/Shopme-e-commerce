package com.shopme.admin.category.service;

import com.shopme.admin.category.repository.CategoryRepository;
import com.shopme.common.entity.Category;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTests {
    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    void CategoryService_CheckUniqueName_ReturnBoolean() {
        Integer catId = 1;
        String catName = "Computers";

        Category category = Category.builder()
                        .id(catId)
                        .name(catName)
                        .build();

        when(categoryRepository.findByName(Mockito.anyString())).thenReturn(category); // Check if the id belongs to the edited category
//        when(categoryRepository.findByName(Mockito.anyString())).thenReturn(null); // True

        boolean result = categoryService.checkUniqueName("testName", catId);
//        boolean result = categoryService.checkUniqueName("testName", 7);

        assertThat(result).isTrue();
    }

    @Test
    void CategoryService_CheckUniqueAlias_ReturnBoolean() {
        Integer catId = 1;
        String catAlias = "tech_accessories";

        Category category = Category.builder()
                .id(catId)
                .alias(catAlias)
                .build();

        when(categoryRepository.findByAlias(Mockito.anyString())).thenReturn(category); // It will check if the id belongs to the edited category
//        when(categoryRepository.findByName(Mockito.anyString())).thenReturn(null); // True

        boolean result = categoryService.checkUniqueAlias("testAlias", catId);
//        boolean result = categoryService.checkUniqueName("testAlias", 7);

        assertThat(result).isTrue();
    }
}
