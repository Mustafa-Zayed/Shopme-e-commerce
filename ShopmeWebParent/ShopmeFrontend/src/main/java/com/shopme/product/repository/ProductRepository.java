package com.shopme.product.repository;

import com.shopme.common.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface ProductRepository extends CrudRepository<Product, Integer>, PagingAndSortingRepository<Product, Integer> {
    @Query("SELECT p FROM Product p WHERE p.enabled = true AND (p.category.id = ?1 OR p.category.allParentIDs " +
            "LIKE CONCAT('%-', ?1, '-%'))" +
            "ORDER BY p.name ASC")
    Page<Product> findAll(Integer prodCatId, Pageable pageable);

    Product findByAlias(String prodAlias);

    // We use the native query because full-text search is specific to MySQL, not provided by JPA.
    @Query(value = "SELECT * FROM products " +
            "WHERE enabled = true " +
            "AND MATCH(name, short_description, full_description) AGAINST(?1)",
            nativeQuery = true)
    Page<Product> fullTextSearch(String keyword, Pageable pageable);
}
