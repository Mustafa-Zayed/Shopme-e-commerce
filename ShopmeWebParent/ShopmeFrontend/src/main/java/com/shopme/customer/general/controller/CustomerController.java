package com.shopme.customer.general.controller;

import com.shopme.common.entity.Customer;
import com.shopme.common.entity.setting.country.Country;
import com.shopme.customer.country.service.CountryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@RequiredArgsConstructor
@Controller
public class CustomerController {
    private final CountryService countryService;

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        List<Country> countries = countryService.findAll();

        model.addAttribute("pageTitle", "Customer Registration");
        model.addAttribute("customer", new Customer());
        model.addAttribute("countries", countries);

        return "customers/register/register_form";
    }

}
