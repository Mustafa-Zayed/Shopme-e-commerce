package com.shopme.admin.product.repository;

import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ProductRepository extends CrudRepository<Product, Integer>, PagingAndSortingRepository<Product, Integer> {
    @Query("SELECT p FROM Product p WHERE p.name LIKE %?1%")
    Page<Product> findAll(String keyword, Pageable pageable);

    Product findByName(String name);

    @Modifying
    @Query("UPDATE Product p SET p.enabled = ?2 WHERE p.id = ?1")
    void updateProductEnabledStatus(int id, boolean status);
}
