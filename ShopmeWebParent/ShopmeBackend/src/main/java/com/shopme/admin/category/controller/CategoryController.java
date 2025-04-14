package com.shopme.admin.category.controller;

import com.shopme.admin.category.service.CategoryService;
import com.shopme.common.entity.Category;
import com.shopme.common.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

import static com.shopme.admin.user.service.UserService.USERS_PER_PAGE;

@RequiredArgsConstructor
@Controller
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/categories")
    public String listFirstPage(Model model) {
        return listByPageWithSorting(1, "name", "asc", "", model);
    }

    @GetMapping("/categories/page/{pageNumber}")
    public String listByPageWithSorting(
            @PathVariable int pageNumber,
            @RequestParam(name = "sortField", defaultValue = "name") String sortField,
            @RequestParam(name = "sortDir", defaultValue = "asc") String sortDir,
            @RequestParam(name = "keyword", defaultValue = "") String keyword,
            Model model) {
        Page<Category> categoryPage = categoryService.listByPageWithSorting(pageNumber, sortField, sortDir, keyword);
        return addToModel(pageNumber, categoryPage, sortField, sortDir, keyword, model);
    }

    private String addToModel(int pageNumber, Page<Category> usersPage,
                              String sortField, String sortDir, String keyword, Model model) {
        List<Category> categories = usersPage.getContent();
        int totalPages = usersPage.getTotalPages();
        long totalItems = usersPage.getTotalElements();

        long startCount = ((long) (pageNumber - 1) * USERS_PER_PAGE) + 1;
        long endCount = (startCount + USERS_PER_PAGE) - 1;
        if (endCount > totalItems) endCount = totalItems;
        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);

        model.addAttribute("categories", categories);
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
}
