package com.shopme.admin.category.controller;

import com.shopme.admin.category.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class CategoryRestController {

    private final CategoryService categoryService;

    // Consumed by Ajax call in category_form.html
    @PostMapping("/categories/check_unique_name_alias")
    public String checkUniqueNameAlias(@RequestParam String name,
                                       @RequestParam String alias,
                                       @RequestParam(defaultValue = "") Integer id
    ) {

        boolean checkUniqueName = categoryService.checkUniqueName(name, id);
        boolean checkUniqueAlias = categoryService.checkUniqueAlias(alias, id);

        if (checkUniqueName && checkUniqueAlias) {
            return "OK";
        } else if (!checkUniqueName) {
            return "DuplicatedName";
        } else {
            return "DuplicatedAlias";
        }
    }
}
