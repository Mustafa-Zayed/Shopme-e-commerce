package com.shopme.admin.product.controller;

import com.shopme.admin.brand.service.BrandService;
import com.shopme.admin.product.service.ProductService;
import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@Controller
public class ProductController {

    private final ProductService productService;
    private final BrandService brandService;

    @GetMapping("/products")
    public String listFirstPage(Model model) {
        return listByPageWithSorting(1, "name", "asc", "", model);
    }

    @GetMapping("/products/page/{pageNumber}")
    public String listByPageWithSorting(
            @PathVariable int pageNumber,
            @RequestParam(name = "sortField", defaultValue = "name") String sortField,
            @RequestParam(name = "sortDir", defaultValue = "asc") String sortDir,
            @RequestParam(name = "keyword", defaultValue = "") String keyword,
            Model model) {

        Page<Product> productsPage = productService.listByPageWithSorting(pageNumber, sortField, sortDir, keyword);
        return addToModel(pageNumber, productsPage, sortField, sortDir, keyword, model);
    }

    private String addToModel(@PathVariable int pageNumber, Page<Product> productsPage,
                              String sortField, String sortDir, String keyword, Model model) {
        List<Product> listProducts = productsPage.getContent();
        int totalPages = productsPage.getTotalPages();
        long totalItems = productsPage.getTotalElements();

        long startCount = ((long) (pageNumber - 1) * 4) + 1;
        long endCount = (startCount + 4) - 1;
        if (endCount > totalItems) endCount = totalItems;
        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);

        model.addAttribute("listProducts", listProducts);
        model.addAttribute("currentPage", pageNumber);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalItems", totalItems);

        String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";

        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", reverseSortDir);

        model.addAttribute("keyword", keyword);

        return "products/products";
    }

    @GetMapping("/products/new")
    public String newProduct(Model model) {
        Product product = Product.builder()
                .enabled(true)
                .inStock(true)
                .cost(0.0f)
                .listPrice(0.0f)
                .discountPercent(0.0f)
                .build();
        List<Brand> listBrands = brandService.listAll(Sort.by("name").ascending());

        model.addAttribute("product", product);
        model.addAttribute("listBrands", listBrands);
        model.addAttribute("pageTitle", "Create New Product");

        return "products/product_form";
    }

    @PostMapping("/products/save")
    public String saveProduct(@ModelAttribute Product product) {
        System.out.println("Product: " + product);
        return "redirect:/products";
    }
}
