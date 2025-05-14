package com.shopme.admin.brand.controller;

import com.shopme.admin.brand.service.BrandService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class BrandRestController {

    private final BrandService brandService;

    // Consumed by Ajax call in brand_form.html
    @PostMapping("/brands/check_unique_name")
    public String checkUniqueNameAlias(@RequestParam String name,
                                       @RequestParam(defaultValue = "") Integer id
    ) {
        return brandService.checkUniqueName(name, id) ? "OK" : "Duplicated";
    }
}
