package com.shopme.admin.product.service;

import com.shopme.admin.category.exception.CategoryNotFoundException;
import com.shopme.admin.product.exception.ProductNotFoundException;
import com.shopme.admin.product.repository.ProductRepository;
import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Product;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Date;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ProductService {
    private final ProductRepository productRepository;

    public List<Product> listAll() {
        return (List<Product>) productRepository.findAll(Sort.by("id").ascending());
    }

    public Page<Product> listByPageWithSorting(int pageNumber, String sortField, String sortDir,
                                               String keyword) {
        Sort sort = Sort.by(sortField);
        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();

        // page number is a 0-based index, but sent from the client as a 1-based index, so we need to subtract 1.
        Pageable pageable = PageRequest.of(pageNumber - 1, 4, sort);

        if (keyword.isEmpty())
            return productRepository.findAll(pageable);

        return productRepository.findAll(keyword, pageable);
    }

    public Product save(Product product, RedirectAttributes redirectAttributes) {
        String message = product.getId() == null ?
                "New Product has been created!" : "Product has been updated successfully!";

        product.setUpdatedTime(new Date());
        product.setMainImage("default.png");

        if (product.getId() == null) {
            product.setCreatedTime(new Date());
        }

        if (product.getAlias() == null || product.getAlias().isEmpty())
            product.setAlias(product.getName().replaceAll(" ", "-"));
        else
            product.setAlias(product.getAlias().replaceAll(" ", "-"));

        redirectAttributes.addFlashAttribute("message", message);
        return productRepository.save(product);
    }

    public boolean checkUniqueName(String name, Integer id) {
        Product byName  = productRepository.findByName(name);
        if (byName == null)
            return true;

        // Check if the name belongs to the edited product (i.e. user doesn't need to change the product name)
        // If new product case, returns false as the id param is null
        return byName.getId().equals(id);
    }

    public Product findById(int id) throws ProductNotFoundException {
        return productRepository.findById(id).orElseThrow(
                () -> new ProductNotFoundException("Could not find any product with ID: " + id)
        );
    }

    @Transactional
    public void updateProductEnabledStatus(int id, boolean status) {
        productRepository.updateProductEnabledStatus(id, status);
    }
}
