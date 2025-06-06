package com.shopme.admin.brand.service;

import com.shopme.admin.brand.repository.BrandRepository;
import com.shopme.common.entity.Brand;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BrandServiceTests {
    @Mock
    private BrandRepository brandRepository;

    @InjectMocks
    private BrandService brandService;

    @Test
    void BrandService_CheckUniqueName_ReturnBoolean() {
        Integer brandId = 1;
        String brandName = "Acer";

        Brand brand = Brand.builder()
                        .id(brandId)
                        .name(brandName)
                        .build();

        when(brandRepository.findByName(Mockito.anyString())).thenReturn(brand); // Check if the id belongs to the edited brand
//        when(brandRepository.findByName(Mockito.anyString())).thenReturn(null); // True

        boolean result = brandService.checkUniqueName("testName", brandId);
//        boolean result = brandService.checkUniqueName("testName", 7);

        assertThat(result).isTrue();
    }
}
