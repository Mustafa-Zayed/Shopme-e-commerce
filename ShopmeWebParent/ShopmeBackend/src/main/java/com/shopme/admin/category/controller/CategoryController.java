package com.shopme.admin.category.controller;

import com.shopme.admin.category.export.CategoryCsvExporter;
import com.shopme.admin.category.pagination.CategoryPageInfo;
import com.shopme.admin.category.exception.CategoryNotFoundException;
import com.shopme.admin.category.exception.HasChildrenException;
import com.shopme.admin.category.service.CategoryService;
import com.shopme.common.entity.Category;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static com.shopme.admin.category.service.CategoryService.ROOT_CATS_PER_PAGE;

@RequiredArgsConstructor
@Controller
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/categories")
    public String listFirstPage(Model model) {
        return listByPageWithSorting(1,"asc", "", model);
    }

    @GetMapping("/categories/page/{pageNumber}")
    public String listByPageWithSorting(
            @PathVariable int pageNumber,
            @RequestParam(name = "sortDir", defaultValue = "asc") String sortDir,
            @RequestParam(name = "keyword", defaultValue = "") String keyword,
            Model model) {

        // default sort field
        String sortField = "name";
        CategoryPageInfo categoryPageInfo = new CategoryPageInfo();

        List<Category> hierarchicalCategories = categoryService
                .listByPageWithSorting(categoryPageInfo, pageNumber, sortField, sortDir, keyword);
        return addToModel(pageNumber, hierarchicalCategories, categoryPageInfo, sortField, sortDir, keyword, model);
    }

    private String addToModel(int pageNumber, List<Category> listCategories,
                              CategoryPageInfo categoryPageInfo,
                              String sortField, String sortDir, String keyword, Model model) {
        int totalPages = categoryPageInfo.getTotalPages();
        long totalItems = categoryPageInfo.getTotalElements();

        long startCount = ((long) (pageNumber - 1) * ROOT_CATS_PER_PAGE) + 1;
        long endCount = (startCount + ROOT_CATS_PER_PAGE) - 1;
        if (endCount > totalItems) endCount = totalItems;
        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);

        model.addAttribute("listCategories", listCategories);
        model.addAttribute("currentPage", pageNumber);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalItems", totalItems);

        String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";

        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", reverseSortDir);

        model.addAttribute("keyword", keyword);

        return "categories/categories";
    }

    @GetMapping("/categories/new")
    public String newCategory(Model model) {
        model.addAttribute("category", Category.builder().enabled(true).build());
//        model.addAttribute("mapCategories", categoryService.listCategoriesUsedInFormMapApproach());
        model.addAttribute("listCategories",
                categoryService.listCategoriesUsedInFormListApproach(Sort.by("name").ascending()));
        model.addAttribute("pageTitle", "Create New Category");
        return "categories/category_form";
    }

    @PostMapping("/categories/save")
    public String saveCategory(@ModelAttribute("cat") Category category,
                           @RequestPart(value = "catImage") MultipartFile multipart,
                           RedirectAttributes redirectAttributes) throws IOException {
        categoryService.save(category, multipart, redirectAttributes);

        Integer catId = category.getId();
        String catAlias = category.getAlias();

        String keyword = catId + " " + catAlias;
        String encodedKeyword = URLEncoder.encode(keyword, StandardCharsets.UTF_8);
        System.out.println("encodedKeyword: " + encodedKeyword);
        return "redirect:/categories/page/1?sortField=id&sortDir=asc&keyword=" + encodedKeyword;
    }

    @GetMapping("/categories/edit/{id}")
    public String editCategory(@PathVariable int id, Model model, RedirectAttributes redirectAttributes) {
        Category category;
        try {
            category = categoryService.findById(id);
        } catch (CategoryNotFoundException ex) {
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
            redirectAttributes.addFlashAttribute("resultClass", "danger");
            return "redirect:/categories";
        }

        model.addAttribute("category", category);
        model.addAttribute("listCategories",
                categoryService.listCategoriesUsedInFormListApproach(Sort.by("name").ascending()));
        model.addAttribute("pageTitle", "Edit Category (ID: " + id + ")");
        return "categories/category_form";
    }

    @GetMapping("/categories/delete/{id}")
    public String deleteCategory(@PathVariable int id, RedirectAttributes redirectAttributes) {
        try {
            categoryService.delete(id);
            redirectAttributes.addFlashAttribute("message", "Category ID " + id + " has been deleted successfully!");
        } catch (CategoryNotFoundException | HasChildrenException ex) {
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
            redirectAttributes.addFlashAttribute("resultClass", "danger");
        }
        return "redirect:/categories";
    }

    @GetMapping("/categories/{id}/enabled/{status}")
    public String updateCategoryEnabledStatus(@PathVariable int id,
                                          @PathVariable(value = "status") boolean statusBefore,
                                          RedirectAttributes redirectAttributes) {
        String catAlias;
        try {
            categoryService.updateCategoryEnabledStatus(id, !statusBefore);
            Category category = categoryService.findById(id);
            catAlias = category.getAlias();

            if (statusBefore)
                redirectAttributes.addFlashAttribute("message", "Category ID " + id + " has been disabled");
            else
                redirectAttributes.addFlashAttribute("message", "Category ID " + id + " has been enabled");
        } catch (CategoryNotFoundException ex) {
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
            redirectAttributes.addFlashAttribute("resultClass", "danger");
            return "redirect:/categories";
        }
        // URLs with spaces are invalid and can lead to unexpected behavior. Even though %20 is
        // the encoded form of a space, Spring's redirect handling might not properly encode or
        // interpret the URL, causing issues with flash attributes.
        // URLs must be properly encoded (no spaces allowed)
        String keyword = id + " " + catAlias;
        String encodedKeyword = URLEncoder.encode(keyword, StandardCharsets.UTF_8);
        System.out.println("encodedKeyword: " + encodedKeyword);

        return "redirect:/categories/page/1?keyword=" + encodedKeyword;
    }

    @GetMapping("/categories/export/csv")
    public void exportToCSV(HttpServletResponse response) throws IOException {
        List<Category> categories = categoryService.listCategoriesUsedInFormListApproach(
                Sort.by("name").ascending());
        CategoryCsvExporter csvExporter = new CategoryCsvExporter();
        csvExporter.export(categories, response);
    }
}
