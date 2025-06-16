package com.shopme.admin.product.repository;

import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Category;
import com.shopme.common.entity.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
public class ProductRepositoryTests {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    @Test
    void ProductRepository_Save_ReturnSavedProduct() {
        Category laptops = testEntityManager.find(Category.class, 6);
        Category cellPhones = testEntityManager.find(Category.class, 4);
        Category tablets = testEntityManager.find(Category.class, 7);

        Brand acer = testEntityManager.find(Brand.class, 37);
        Brand apple = testEntityManager.find(Brand.class, 9);
        Brand samsung = testEntityManager.find(Brand.class, 10);

        Product laptop = Product.builder()
                .name("Laptop")
                .alias("laptop")
                .shortDescription("Laptop description")
                .fullDescription("Laptop description")
                .mainImage("mainImage")
                .brand(apple)
                .category(laptops)
                .build();

        Product phone = Product.builder()
                .name("Phone")
                .alias("phone")
                .shortDescription("Phone description")
                .fullDescription("Phone description")
                .mainImage("mainImage")
                .brand(samsung)
                .category(cellPhones)
                .build();

        Product tablet = Product.builder()
                .name("Tablet")
                .alias("tablet")
                .shortDescription("Tablet description")
                .fullDescription("Tablet description")
                .mainImage("mainImage")
                .brand(acer)
                .category(tablets)
                .build();

        Product savedLaptop = productRepository.save(laptop);
        Product savedPhone = productRepository.save(phone);
        Product savedTablet = productRepository.save(tablet);

        System.out.println(laptop);
        System.out.println(phone);
        System.out.println(tablet);

        assertThat(savedLaptop.getId()).isGreaterThan(0);
        assertThat(savedPhone.getId()).isGreaterThan(0);
        assertThat(savedTablet.getId()).isGreaterThan(0);
    }

    @Test
    void ProductRepository_FindAll_ReturnAllProducts() {
        List<Product> products = (List<Product>) productRepository.findAll();
        products.forEach(System.out::println);

        assertThat(products).isNotNull();
        assertThat(products).hasSizeGreaterThan(0);
    }

    @Test
    void ProductRepository_FindById_ReturnProduct() {
        Optional<Product> product = productRepository.findById(4);
        System.out.println(product);

        assertThat(product).isPresent();
        assertThat(product.get().getId()).isEqualTo(4);
    }

    @Test
    void ProductRepository_Update_ReturnUpdatedProduct() {
        Product laptop = productRepository.findById(4).get();
        laptop.setName("Apple laptop");

        Product savedLaptop = productRepository.findById(4).get();
        System.out.println(savedLaptop);
        assertThat(savedLaptop.getName()).isEqualTo("Apple laptop");
    }

    @Test
    void ProductRepository_Delete_ReturnDeletedProduct() {
        productRepository.deleteById(5);
        assertThat(productRepository.findById(5).isPresent()).isFalse();
    }

    @Test
    void ProductRepository_SaveProductWithImages_ReturnSavedProduct() {

        Product product = productRepository.findById(8).get();

        product.addExtraImage("extraImage1.jpg");
        product.addExtraImage("extraImage2.jpg");
        product.addExtraImage("extraImage3.jpg");

        Product savedProduct = productRepository.save(product);

        System.out.println(savedProduct);

        assertThat(savedProduct.getExtraImages().size()).isEqualTo(3);
    }
}
