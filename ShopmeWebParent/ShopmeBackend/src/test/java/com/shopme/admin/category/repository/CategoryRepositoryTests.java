package com.shopme.admin.category.repository;

import com.shopme.common.entity.Category;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class CategoryRepositoryTests {

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    void CategoryRepository_Save_ReturnRootCategories() {
        Category category = Category.builder()
                .name("Computers")
                .alias("computers")
                .image("default.png")
                .build();
        Category category2 = Category.builder()
                .name("Electronics")
                .alias("electronics")
                .image("default.png").build();

        Category savedCategory = categoryRepository.save(category);
        Category savedCategory2 = categoryRepository.save(category2);
        System.out.println(savedCategory);
        System.out.println(savedCategory2);

        assertThat(savedCategory.getId()).isGreaterThan(0);
        assertThat(savedCategory).isNotNull();
        assertThat(savedCategory2.getId()).isGreaterThan(0);
        assertThat(savedCategory2).isNotNull();
    }

    @Test
    void CategoryRepository_Save_ReturnSubCategories() {
//        Category category = Category.builder()
//                .name("Desktops")
//                .alias("desktops")
//                .image("default.png")
//                .parent(Category.builder().id(1).build())
//                .build();
//        Category category2 = Category.builder()
//                .name("Laptops")
//                .alias("laptops")
//                .image("default.png")
//                .parent(Category.builder().id(1).build())
//                .build();
//        Category category3 = Category.builder()
//                .name("Computer Components")
//                .alias("computer components")
//                .image("default.png")
//                .parent(Category.builder().id(1).build())
//                .build();
//        Category category4 = Category.builder()
//                .name("Memory")
//                .alias("memory")
//                .image("default.png")
//                .parent(Category.builder().id(5).build())
//                .build();
        Category category5 = Category.builder()
                .name("Cameras")
                .alias("cameras")
                .image("default.png")
                .parent(Category.builder().id(2).build())
                .build();
        Category category6 = Category.builder()
                .name("Smart Phones")
                .alias("smart phones")
                .image("default.png")
                .parent(Category.builder().id(2).build())
                .build();

//        Category savedCategory = categoryRepository.save(category);
//        Category savedCategory2 = categoryRepository.save(category2);
//        Category savedCategory3 = categoryRepository.save(category3);
//        Category savedCategory4 = categoryRepository.save(category4);
        Category savedCategory5 = categoryRepository.save(category5);
        Category savedCategory6 = categoryRepository.save(category6);
//        System.out.println(savedCategory);
//        System.out.println(savedCategory2);
//        System.out.println(savedCategory3);
//        System.out.println(savedCategory4);
        System.out.println(savedCategory5);
        System.out.println(savedCategory6);

//        assertThat(savedCategory.getId()).isGreaterThan(0);
//        assertThat(savedCategory).isNotNull();
//        assertThat(savedCategory2.getId()).isGreaterThan(0);
//        assertThat(savedCategory2).isNotNull();
//        assertThat(savedCategory3.getId()).isGreaterThan(0);
//        assertThat(savedCategory3).isNotNull();
//        assertThat(savedCategory4.getId()).isGreaterThan(0);
//        assertThat(savedCategory4).isNotNull();
        assertThat(savedCategory5.getId()).isGreaterThan(0);
        assertThat(savedCategory5).isNotNull();
        assertThat(savedCategory6.getId()).isGreaterThan(0);
        assertThat(savedCategory6).isNotNull();
    }

    @Test
    void CategoryRepository_FindAll_ReturnRootCategoriesWithSubCategoriesInHierarchy(){
        Iterable<Category> categories = categoryRepository.findAll();
        categories.forEach(cat -> {
            if (cat.getParent() == null)
                printSubCategories(cat, 1);
        });
    }

    private void printSubCategories(Category parent, int level) {
        if (parent.getParent() == null) // root category
            System.out.println(parent.getName());

        Set<Category> children = parent.getChildren();
        for (Category child : children) {
            System.out.println("--".repeat(level) + child.getName());
            if (!child.getChildren().isEmpty())
                printSubCategories(child, level + 1);
        }
    }

    @Test
    void CategoryRepository_FindByName_ReturnCategory() {
        String catName = "Computers";
        Category category = categoryRepository.findByName(catName);

        assertThat(category).isNotNull();
        assertThat(category.getName()).isEqualTo(catName);
    }

    @Test
    void CategoryRepository_FindByAlias_ReturnCategory() {
        String catAlias = "laptop_computers";
        Category category = categoryRepository.findByAlias(catAlias);

        assertThat(category).isNotNull();
        assertThat(category.getAlias()).isEqualTo(catAlias);
    }
}
