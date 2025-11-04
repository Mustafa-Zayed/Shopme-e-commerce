package com.shopme.category.service;

import com.shopme.category.repository.CategoryRepository;
import com.shopme.common.entity.Category;
import com.shopme.common.exception.CategoryNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public List<Category> listNoChildrenCategories() {
        List<Category> allEnabled = categoryRepository.findAllEnabled();
        return allEnabled.stream()
                .filter(category -> !category.hasChildren())
                .toList();
    }

    public Category findByAlias(String alias) throws CategoryNotFoundException {
        Category category = categoryRepository.findByAliasAndEnabledIsTrue(alias);
        if (category == null)
            throw new CategoryNotFoundException("Could not find any categories with alias: " + alias);
        return category;
    }

    public List<Category> findAllParents(Category category) {
        List<Category> listParents = new ArrayList<>();
        String allParentIDs = category.getAllParentIDs();

        if (allParentIDs == null || allParentIDs.isEmpty()) // root category
            return listParents;

        String[] parentIDsStrings = allParentIDs.split("-");
        List<Integer> parentIDs = Arrays.stream(parentIDsStrings)
                .filter(str -> !str.isEmpty())
                .map(Integer::parseInt).toList();
        parentIDs.forEach(parentID -> listParents.add(categoryRepository.findById(parentID).get()));
        return listParents;
    }

    public List<Category> findAllDirectChildrenCategories(Category category) {
        return categoryRepository.findByParentOrderByNameAsc(category);
        // return category.getChildren().stream().toList(); // also works with adding @OrderBy("name ASC") to children field of Category entity
    }
}
