package com.shopme.admin.setting.country.service;

import com.shopme.admin.setting.country.repository.CountryRepository;
import com.shopme.common.entity.setting.country.Country;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CountryService {
    private final CountryRepository countryRepository;

    public List<Country> findAll() {
        return countryRepository.findAllByOrderByNameAsc();
    }

    public Country save(Country country) {
        return countryRepository.save(country);
    }

    public void delete(Integer id) {
        countryRepository.deleteById(id);
    }
}
