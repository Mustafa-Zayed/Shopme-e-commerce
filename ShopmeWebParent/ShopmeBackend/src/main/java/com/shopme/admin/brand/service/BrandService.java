package com.shopme.admin.brand.service;

import com.shopme.admin.brand.repository.BrandRepository;
import com.shopme.common.entity.Brand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class BrandService {

    private final BrandRepository brandRepository;

    public List<Brand> listAll() {
        return (List<Brand>) brandRepository.findAll();
    }
}
