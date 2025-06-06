package com.shopme.admin.product.controller;

import com.shopme.admin.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class ProductRestController {

    private final ProductService productService;

    // Consumed by Ajax call in brand_form.html
    @PostMapping("/products/check_unique_name")
    public String checkUniqueName(@RequestParam String name,
                                       @RequestParam(defaultValue = "") Integer id
    ) {
        return productService.checkUniqueName(name, id) ? "OK" : "Duplicated";
    }

}
