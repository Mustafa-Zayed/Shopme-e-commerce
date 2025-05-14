package com.shopme.admin.brand.controller;

import com.shopme.admin.brand.exception.BrandNotFoundException;
import com.shopme.admin.brand.service.BrandService;
import com.shopme.admin.category.service.CategoryService;
import com.shopme.common.entity.Brand;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
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

    @PostMapping("/brands/save")
    public String saveBrand(@ModelAttribute("brand") Brand brand,
                            @RequestPart(value = "brandImage") MultipartFile multipart,
                            RedirectAttributes redirectAttributes) throws IOException {
        brandService.save(brand, multipart, redirectAttributes);

        return "redirect:/brands";
    }

    @GetMapping("/brands/edit/{id}")
    public String editBrand(@PathVariable int id, Model model, RedirectAttributes redirectAttributes) {
        Brand brand;
        try {
            brand = brandService.findById(id);
        } catch (BrandNotFoundException ex) {
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
            redirectAttributes.addFlashAttribute("resultClass", "danger");
            return "redirect:/brands";
        }

        model.addAttribute("brand", brand);
        model.addAttribute("listCategories",
                categoryService.listCategoriesUsedInFormListApproach(Sort.by("name").ascending()));
        model.addAttribute("pageTitle", "Edit Brand (ID: " + id + ")");
        return "brands/brand_form";
    }

    @GetMapping("/brands/delete/{id}")
    public String deleteBrand(@PathVariable int id, RedirectAttributes redirectAttributes) {
        try {
            brandService.delete(id);
            redirectAttributes.addFlashAttribute("message", "Category ID " + id + " has been deleted successfully!");
        } catch (BrandNotFoundException ex) {
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
            redirectAttributes.addFlashAttribute("resultClass", "danger");
        }
        return "redirect:/brands";
    }
}
