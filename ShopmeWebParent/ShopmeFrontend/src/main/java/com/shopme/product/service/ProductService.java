package com.shopme.product.service;

import com.shopme.common.entity.product.Product;
import com.shopme.common.exception.ProductNotFoundException;
import com.shopme.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ProductService {
    private final ProductRepository productRepository;
    public static final int PRODUCTS_PER_PAGE = 10;
    public static final int SEARCH_RESULTS_PER_PAGE = 10;

    public Page<Product> findAllProductsByCategory(Integer prodCatId, int pageNumber) {
        // page number is a 0-based index, but sent from the client as a 1-based index, so we need to subtract 1.
        Pageable pageable = PageRequest.of(pageNumber - 1, PRODUCTS_PER_PAGE);
        return productRepository.findAll(prodCatId, pageable);
    }

    public Product findByAlias(String prodAlias) throws ProductNotFoundException {
        Product product = productRepository.findByAlias(prodAlias);
        if (product == null)
            throw new ProductNotFoundException("Could not find any products with alias: " + prodAlias);
        return product;
    }

    public Page<Product> fullTextSearch(String keyword, Integer pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber - 1, SEARCH_RESULTS_PER_PAGE);
        return productRepository.fullTextSearch(keyword, pageable);
    }
}
