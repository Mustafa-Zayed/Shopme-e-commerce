package com.shopme.admin.brand.repository;

import com.shopme.admin.category.repository.CategoryRepository;
import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Category;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
public class BrandRepositoryTests {

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    void BrandRepository_Save_ReturnSavedBrand() {
        Category laptops = categoryRepository.findByName("Laptops");
        Category cellPhones = categoryRepository.findByName("Cell phones & Accessories");
        Category tablets = categoryRepository.findByName("Tablets");
        Category memory = categoryRepository.findByName("Memory");
        Category hardDrives = categoryRepository.findByName("Internal Hard Drives");

        Brand acer = Brand.builder()
                .name("Acer")
                .logo("brand-logo.png")
                .categories(Set.of(laptops))
                .build();

        Brand apple = Brand.builder()
                .name("Apple")
                .logo("brand-logo.png")
                .categories(Set.of(cellPhones, tablets))
                .build();

        Brand samsung = Brand.builder()
                .name("Samsung")
                .logo("brand-logo.png")
                .categories(Set.of(memory, hardDrives))
                .build();

        Brand savedAcer = brandRepository.save(acer);
        Brand savedApple = brandRepository.save(apple);
        Brand savedSamsung = brandRepository.save(samsung);

        System.out.println(savedAcer);
        System.out.println(savedApple);
        System.out.println(savedSamsung);

        assertThat(savedAcer.getId()).isGreaterThan(0);
        assertThat(savedApple.getId()).isGreaterThan(0);
        assertThat(savedSamsung.getId()).isGreaterThan(0);
    }

    @Test
    void BrandRepository_FindAll_ReturnAllBrands() {
        List<Brand> brands = (List<Brand>) brandRepository.findAll();
        brands.forEach(System.out::println);

        assertThat(brands).isNotNull();
        assertThat(brands).hasSizeGreaterThan(0);
    }

    @Test
    void BrandRepository_FindById_ReturnBrand() {
        Brand brand = brandRepository.findById(1).get();
        System.out.println(brand);

        assertThat(brand).isNotNull();
        assertThat(brand.getId()).isEqualTo(1);
    }

    @Test
    void BrandRepository_Update_ReturnUpdatedBrand() {
        Brand samsung = brandRepository.findById(3).get();

        samsung.setName("Samsung Electronics");

        Brand savedSamsung = brandRepository.findById(3).get();
        System.out.println(savedSamsung);
        assertThat(savedSamsung.getName()).isEqualTo("Samsung Electronics");
    }

    @Test
    void BrandRepository_Delete_ReturnDeletedBrand() {
        brandRepository.deleteById(2);
        assertThat(brandRepository.findById(2).isPresent()).isFalse();
    }
}
