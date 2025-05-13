package com.shopme.admin.brand.controller;

import com.shopme.admin.brand.service.BrandService;
import com.shopme.admin.category.service.CategoryService;
import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RequiredArgsConstructor
@Controller
public class BrandController {

    private final BrandService brandService;
    private final CategoryService categoryService;

    @GetMapping("/brands")
    public String listFirstPage(Model model) {
        List<Brand> listBrands = brandService.listAll();
        model.addAttribute("listBrands", listBrands);
        return "brands/brands";
    }

    @GetMapping("/brands/new")
    public String newBrand(Model model) {
        model.addAttribute("brand", new Brand());
        model.addAttribute("listCategories",
                categoryService.listCategoriesUsedInFormListApproach(Sort.by("name").ascending()));
        model.addAttribute("pageTitle", "Create New Brand");
        return "brands/brand_form";
    }
}
