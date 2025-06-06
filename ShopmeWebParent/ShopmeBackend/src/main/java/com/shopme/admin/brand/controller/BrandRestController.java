package com.shopme.admin.brand.controller;

import com.shopme.admin.brand.exception.BrandNotFoundException;
import com.shopme.admin.brand.exception.BrandNotFoundRestException;
import com.shopme.admin.brand.service.BrandService;
import com.shopme.admin.category.dto.CategoryDTO;
import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@RestController
public class BrandRestController {

    private final BrandService brandService;

    // Consumed by Ajax call in brand_form.html
    @PostMapping("/brands/check_unique_name")
    public String checkUniqueName(@RequestParam String name,
                                       @RequestParam(defaultValue = "") Integer id
    ) {
        return brandService.checkUniqueName(name, id) ? "OK" : "Duplicated";
    }

    // Because this Rest API return an object to the client, we need to make it light,
    // not return the original object but only the id and name
    @GetMapping("/brands/{id}/categories")
    public List<CategoryDTO> findCategoriesByBrand(@PathVariable(name = "id") Integer brandId) throws BrandNotFoundRestException {
        try {
            Brand brand = brandService.findById(brandId);
            Set<Category> categories = brand.getCategories();
            List<CategoryDTO> categoryList = new ArrayList<>();
            categories.forEach(cat -> categoryList.add(new CategoryDTO(cat.getId(), cat.getName())));

            return categoryList;
        } catch (BrandNotFoundException e) {
            throw new BrandNotFoundRestException(); // new exception to assign the HTTP status code
        }
    }
}
