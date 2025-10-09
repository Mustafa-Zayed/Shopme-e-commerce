package com.shopme.category.repository;

import com.shopme.common.entity.Category;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
public class CategoryRepositoryTests {

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    void CategoryRepository_FindAllEnabled_ReturnEnabledCategories() {
        List<Category> allEnabled = categoryRepository.findAllEnabled();
        allEnabled.forEach(category ->
                System.out.println(category.getName() + " - " + category.isEnabled())
        );

        assertThat(allEnabled).hasSizeGreaterThan(0);
    }
}
