package com.shopme.customer.country.service;

import com.shopme.common.entity.setting.country.Country;
import com.shopme.customer.country.repository.CountryRepository;
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
}
