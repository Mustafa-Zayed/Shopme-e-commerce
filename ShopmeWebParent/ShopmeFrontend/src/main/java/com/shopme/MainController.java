package com.shopme;

import com.shopme.category.service.CategoryService;
import com.shopme.common.entity.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MainController {

    private final CategoryService categoryService;

    @GetMapping("")
    public String viewHomePage(Model model) {
        List<Category> categories = categoryService.listNoChildrenCategories();
        model.addAttribute("categories", categories);

        return "index";
    }

    @GetMapping("/login")
    public String login(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated())
            return "login";
        return "redirect:/";
    }
}
