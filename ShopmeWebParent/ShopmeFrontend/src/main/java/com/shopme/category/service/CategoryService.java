package com.shopme.category.service;

import com.shopme.category.repository.CategoryRepository;
import com.shopme.common.entity.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
}
