package com.shopme.admin.product.service;

import com.shopme.admin.product.repository.ProductRepository;
import com.shopme.common.entity.product.Product;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTests {
    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    void ProductService_CheckUniqueName_ReturnBoolean() {
        Integer productId = 1;
        String productName = "Laptop";

        Product product = Product.builder()
                        .id(productId)
                        .name(productName)
                        .build();

        when(productRepository.findByName(Mockito.anyString())).thenReturn(product); // Check if the id belongs to the edited product
//        when(productRepository.findByName(Mockito.anyString())).thenReturn(null); // True

        boolean result = productService.checkUniqueName("testName", productId);
//        boolean result = productService.checkUniqueName("testName", 7);

        assertThat(result).isTrue();
    }
}
