package com.shopme.admin.setting.country.controller;

import com.shopme.admin.setting.country.service.CountryService;
import com.shopme.common.entity.setting.country.Country;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class CountryRestController {
    private final CountryService countryService;

    @GetMapping("/countries")
    public List<Country> listAllCountries() {
        return countryService.findAll();
    }

    @PostMapping("/countries/save")
    public Country saveCountry(@RequestBody Country country) {
        return countryService.save(country);
    }


    @DeleteMapping("/countries/delete/{id}")
    public void deleteCountry(@PathVariable("id") Integer id) {
        countryService.delete(id);
    }
}
