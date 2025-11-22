package com.shopme.product.controller;

import com.shopme.category.service.CategoryService;
import com.shopme.common.entity.Category;
import com.shopme.common.entity.product.Product;
import com.shopme.common.exception.ProductNotFoundException;
import com.shopme.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

import static com.shopme.product.service.ProductService.SEARCH_RESULTS_PER_PAGE;

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

    @GetMapping("/search")
    public String searchFirstPage(@RequestParam(name = "keyword") String keyword, Model model) {
        return searchByPage(1, keyword, model);
    }

    @GetMapping("/search/page/{pageNum}")
    public String searchByPage(@PathVariable(name = "pageNum") Integer pageNum,
                               @RequestParam(name = "keyword") String keyword,
                               Model model) {
        Page<Product> productPage = productService.fullTextSearch(keyword, pageNum);

        addToModel(productPage, pageNum, keyword, model);

        return "products/search_results";
    }

    private void addToModel(Page<Product> productPage, Integer pageNum, String keyword, Model model) {

        List<Product> listResult = productPage.getContent();

        long startCount = ((long) (pageNum - 1) * SEARCH_RESULTS_PER_PAGE) + 1;
        long endCount = (startCount + SEARCH_RESULTS_PER_PAGE) - 1;
        if (endCount > productPage.getTotalElements())
            endCount = productPage.getTotalElements();

        model.addAttribute("keyword", keyword);
        model.addAttribute("listResult", listResult);

        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);

        model.addAttribute("currentPage", pageNum);
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("totalItems", productPage.getTotalElements());
        model.addAttribute("pageTitle", keyword + " - Search Results");
    }
}
