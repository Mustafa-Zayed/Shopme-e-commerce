package com.shopme.admin.product.controller;

import com.shopme.admin.brand.service.BrandService;
import com.shopme.admin.product.exception.ProductNotFoundException;
import com.shopme.admin.product.service.ProductService;
import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Date;
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
                .createdTime(new Date(System.currentTimeMillis()))
                .build();
        List<Brand> listBrands = brandService.listAll(Sort.by("name").ascending());

        model.addAttribute("product", product);
        model.addAttribute("listBrands", listBrands);
        model.addAttribute("pageTitle", "Create New Product");

        return "products/product_form";
    }

    @PostMapping("/products/save")
    public String saveProduct(@ModelAttribute("product") Product product,
                              @RequestPart(value = "prodMainImage") MultipartFile multipart,
                              RedirectAttributes redirectAttributes) throws IOException {
        // avoid the field name collision between the Product.mainImage string and the uploaded file name in product_form,
        // so Spring is receiving the uploaded file and trying to bind it to product.mainImage, because
        // both the request part and the model attribute are sharing the same field name — mainImage

        productService.save(product, multipart, redirectAttributes);

        return "redirect:/products";
    }

    @GetMapping("/products/delete/{id}")
    public String deleteProduct(@PathVariable int id, RedirectAttributes redirectAttributes) {
        try {
            productService.delete(id);
            redirectAttributes.addFlashAttribute("message", "Product ID " + id + " has been deleted successfully!");
        } catch (ProductNotFoundException ex) {
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
            redirectAttributes.addFlashAttribute("resultClass", "danger");
        }
        return "redirect:/products";
    }

    @GetMapping("/products/{id}/enabled/{status}")
    public String updateProductEnabledStatus(@PathVariable int id,
                                              @PathVariable(value = "status") boolean statusBefore,
                                              RedirectAttributes redirectAttributes) {
        String productAlias;
        try {
            productService.updateProductEnabledStatus(id, !statusBefore);
            Product product = productService.findById(id);
            productAlias = product.getAlias();

            if (statusBefore)
                redirectAttributes.addFlashAttribute("message", "Product ID " + id + " has been disabled");
            else
                redirectAttributes.addFlashAttribute("message", "Product ID " + id + " has been enabled");
        } catch (ProductNotFoundException ex) {
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
            redirectAttributes.addFlashAttribute("resultClass", "danger");
            return "redirect:/products";
        }
        // URLs with spaces are invalid and can lead to unexpected behavior. Even though %20 is
        // the encoded form of a space, Spring's redirect handling might not properly encode or
        // interpret the URL, causing issues with flash attributes.
        // URLs must be properly encoded (no spaces allowed)
        String keyword = id + " " + productAlias;
        String encodedKeyword = URLEncoder.encode(keyword, StandardCharsets.UTF_8);
        System.out.println("encodedKeyword: " + encodedKeyword);

//        return "redirect:/products/page/1?keyword=" + encodedKeyword;
        return "redirect:/products";
    }
}
