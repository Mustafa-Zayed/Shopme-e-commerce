package com.shopme.product.controller;

import com.shopme.category.service.CategoryService;
import com.shopme.common.entity.Category;
import com.shopme.common.entity.Product;
import com.shopme.common.exception.ProductNotFoundException;
import com.shopme.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final CategoryService categoryService;

    @GetMapping("/p/{prodAlias}")
    public String viewProductDetails(@PathVariable("prodAlias") String prodAlias, Model model) {
        try {
            Product product = productService.findByAlias(prodAlias);

            Category category = product.getCategory();
            List<Category> allCategoryParents = categoryService.findAllParents(category); // for breadcrumb navigation
            allCategoryParents.add(category); // Also, add the current product category to the list

            model.addAttribute("product", product);
            model.addAttribute("allCategoryParents", allCategoryParents);

            model.addAttribute("pageTitle", product.getShortName());
            return "products/product_details";
        } catch (ProductNotFoundException e) {
            return "error/404";
        }
    }
}
