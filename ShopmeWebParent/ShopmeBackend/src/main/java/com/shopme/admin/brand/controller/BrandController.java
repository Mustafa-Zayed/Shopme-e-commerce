package com.shopme.admin.brand.controller;

import com.shopme.admin.brand.service.BrandService;
import com.shopme.common.entity.Brand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RequiredArgsConstructor
@Controller
public class BrandController {

    private final BrandService brandService;

    @GetMapping("/brands")
    public String listFirstPage(Model model) {
        List<Brand> listBrands = brandService.listAll();
        model.addAttribute("listBrands", listBrands);
        return "brand/brands";
    }

}
