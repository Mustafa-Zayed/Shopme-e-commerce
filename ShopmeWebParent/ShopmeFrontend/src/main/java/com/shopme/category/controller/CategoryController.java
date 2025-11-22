package com.shopme.category.controller;

import com.shopme.category.service.CategoryService;
import com.shopme.common.entity.Category;
import com.shopme.common.entity.product.Product;
import com.shopme.common.exception.CategoryNotFoundException;
import com.shopme.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

import static com.shopme.product.service.ProductService.PRODUCTS_PER_PAGE;

@Controller
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;
    private final ProductService productService;

    @GetMapping("/c/{categoryAlias}")
    public String viewCategoryFirstPage(@PathVariable("categoryAlias") String categoryAlias,
                                Model model) {
        return viewCategory(categoryAlias, 1, model);
    }

    @GetMapping("/c/{categoryAlias}/page/{pageNumber}")
    public String viewCategory(@PathVariable("categoryAlias") String categoryAlias,
                               @PathVariable(name = "pageNumber") int pageNumber,
                               Model model) {
        try {
            Category category = categoryService.findByAlias(categoryAlias);

            List<Category> allCategoryParents = categoryService.findAllParents(category); // for breadcrumb navigation
            List<Category> directChildrenCategories = categoryService.findAllDirectChildrenCategories(category); // for displaying subcategories

            addToModel(categoryAlias, category, allCategoryParents, directChildrenCategories, pageNumber, model);

            return "categories/products_by_category";
        } catch (CategoryNotFoundException e) {
            return "error/404";
        }
    }

    private void addToModel(String categoryAlias, Category category, List<Category> allCategoryParents,
                              List<Category> directChildrenCategories, int pageNumber, Model model) {
        Page<Product> productsPage = productService
                .findAllProductsByCategory(category.getId(), pageNumber);
        List<Product> listProducts = productsPage.getContent();

        int totalPages = productsPage.getTotalPages();
        long totalItems = productsPage.getTotalElements();

        long startCount = ((long) (pageNumber - 1) * PRODUCTS_PER_PAGE) + 1;
        long endCount = (startCount + PRODUCTS_PER_PAGE) - 1;
        if (endCount > totalItems)
            endCount = totalItems;

        model.addAttribute("categoryAlias", categoryAlias);
        model.addAttribute("category", category);
        model.addAttribute("allCategoryParents", allCategoryParents);
        model.addAttribute("directChildrenCategories", directChildrenCategories);
        model.addAttribute("listProducts", listProducts);
        model.addAttribute("pageTitle", category.getName());

        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);

        model.addAttribute("currentPage", pageNumber);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalItems", totalItems);

        model.addAttribute("keyword", "");
    }
}
