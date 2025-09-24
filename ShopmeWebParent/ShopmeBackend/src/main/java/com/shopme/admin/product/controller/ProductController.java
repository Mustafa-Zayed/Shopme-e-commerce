package com.shopme.admin.product.controller;

import com.shopme.admin.brand.service.BrandService;
import com.shopme.admin.category.service.CategoryService;
import com.shopme.admin.product.exception.ProductNotFoundException;
import com.shopme.admin.product.service.ProductService;
import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Product;
import com.shopme.common.entity.ProductDetail;
import com.shopme.common.entity.ProductImage;
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
import java.util.Map;
import java.util.Set;

import static com.shopme.admin.product.service.ProductService.PRODUCTS_PER_PAGE;

@RequiredArgsConstructor
@Controller
public class ProductController {

    private final ProductService productService;
    private final BrandService brandService;
    private final CategoryService categoryService;

    @GetMapping("/products")
    public String listFirstPage(Model model) {
        return listByPageWithSorting(1, "name", "asc", "", 0, model);
    }

    @GetMapping("/products/page/{pageNumber}")
    public String listByPageWithSorting(
            @PathVariable int pageNumber,
            @RequestParam(name = "sortField", defaultValue = "name") String sortField,
            @RequestParam(name = "sortDir", defaultValue = "asc") String sortDir,
            @RequestParam(name = "keyword", defaultValue = "") String keyword,
            @RequestParam(name = "prodCatId", defaultValue = "0") Integer prodCatId,
            Model model) {

        Page<Product> productsPage = productService
                .listByPageWithSorting(pageNumber, sortField, sortDir, keyword, prodCatId);
        return addToModel(pageNumber, productsPage, sortField, sortDir, keyword, prodCatId, model);
    }

    private String addToModel(@PathVariable int pageNumber, Page<Product> productsPage,
                              String sortField, String sortDir, String keyword, Integer prodCatId, Model model) {
        List<Product> listProducts = productsPage.getContent();
        int totalPages = productsPage.getTotalPages();
        long totalItems = productsPage.getTotalElements();

        long startCount = ((long) (pageNumber - 1) * PRODUCTS_PER_PAGE) + 1;
        long endCount = (startCount + PRODUCTS_PER_PAGE) - 1;
        if (endCount > totalItems) endCount = totalItems;
        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);

        model.addAttribute("listProducts", listProducts);
        model.addAttribute("listCategories",
                categoryService.listCategoriesUsedInFormListApproach(Sort.by("name").ascending()));

        model.addAttribute("currentPage", pageNumber);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalItems", totalItems);

        String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";

        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", reverseSortDir);

        model.addAttribute("keyword", keyword);
        model.addAttribute("prodCatId", prodCatId);

        return "products/products";
    }

    @GetMapping("/products/new")
    public String newProduct(Model model) {
        Product product = Product.builder()
                .enabled(true)
                .inStock(true)
                .cost(0.0f)
                .price(0.0f)
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
                              RedirectAttributes redirectAttributes,
                              @RequestPart(value = "prodMainImage") MultipartFile mainImageMultipart,
                              @RequestPart(value = "extraImage", required = false) MultipartFile[] extraImageMultiparts, // MultipartFile ...extraImageMultiparts
                              @RequestParam(value = "detailName", required = false) String[] detailNames,
                              @RequestParam(value = "detailValue", required = false) String[] detailValues,
                              @RequestParam(value = "extraImageID", required = false) String[] extraImageIDs,
                              @RequestParam(value = "extraImageName", required = false) String[] extraImageNames,
                              @RequestParam(value = "detailID", required = false) String[] detailIDs) throws IOException {
        // avoid the field name collision between the Product.mainImage string and the uploaded file name in product_form,
        // so Spring is receiving the uploaded file and trying to bind it to product.mainImage, because
        // both the request part and the model attribute are sharing the same field name — mainImage

        productService.save(product, redirectAttributes, mainImageMultipart, extraImageMultiparts,
                detailNames, detailValues, extraImageIDs, extraImageNames, detailIDs);

        return "redirect:/products";
    }

    @GetMapping("/products/edit/{id}")
    public String editProduct(@PathVariable int id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Product product = productService.findById(id);
            List<Brand> listBrands = brandService.listAll(Sort.by("name").ascending());
            Set<ProductImage> extraImages = product.getExtraImages();
            List<ProductDetail> productDetails = product.getProductDetails();

            model.addAttribute("product", product);
            model.addAttribute("listBrands", listBrands);
            model.addAttribute("pageTitle", "Edit Product (ID: " + id + ")");
            model.addAttribute("extraImages", extraImages);
            model.addAttribute("productDetails", productDetails);

            return "products/product_form";
        } catch (ProductNotFoundException ex) {
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
            redirectAttributes.addFlashAttribute("resultClass", "danger");
            return "redirect:/products";
        }
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

    @GetMapping("/products/details/{id}")
    public String viewProductDetails(@PathVariable int id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Product product = productService.findById(id);
            Set<ProductImage> extraImages = product.getExtraImages();
            List<ProductDetail> productDetails = product.getProductDetails();

            model.addAttribute("product", product);
            model.addAttribute("extraImages", extraImages);
            model.addAttribute("productDetails", productDetails);

            return "products/product_details_modal";
        } catch (ProductNotFoundException ex) {
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
            redirectAttributes.addFlashAttribute("resultClass", "danger");
            return "redirect:/products";
        }
    }
}
