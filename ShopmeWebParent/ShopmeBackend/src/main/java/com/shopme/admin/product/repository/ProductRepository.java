package com.shopme.admin.product.repository;

import com.shopme.common.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ProductRepository extends CrudRepository<Product, Integer>, PagingAndSortingRepository<Product, Integer> {
    Integer countById(Integer id);

    Product findByName(String name);

    /**
     * <h3>Differences between LIKE and CONCAT approaches</h3>
     *
     * <h4>Null handling</h4>
     * <ul>
     *   <li><b>OR version:</b> If one field is NULL, no problem — other fields can still match.</li>
     *   <li><b>CONCAT version:</b> If any of the concatenated fields is NULL, the whole CONCAT may become NULL
     *       (depending on DB), and then the LIKE won’t match. <br>
     *       → This means CONCAT version might miss results unless you use <code>COALESCE(field, '')</code>.
     *   </li>
     * </ul>
     *
     * <h4>Performance</h4>
     * <ul>
     *   <li><b>OR version:</b> Database can optimize per-column conditions, sometimes with indexes.</li>
     *   <li><b>CONCAT version:</b> Harder to optimize, since it builds a big string. Usually slower.</li>
     * </ul>
     *
     * <h4>Flexibility</h4>
     * <ul>
     *   <li><b>OR version:</b> Lets you choose which field matched (useful for debugging).</li>
     *   <li><b>CONCAT version:</b> Hides where it matched.</li>
     * </ul>
     */
    @Query("SELECT p FROM Product p WHERE " +
            "p.name LIKE %?1% " +
            "OR p.shortDescription LIKE %?1% " +
            "OR p.fullDescription LIKE %?1% " +
            "OR p.brand.name LIKE %?1% " +
            "OR p.category.name LIKE %?1%")
    Page<Product> findAll(String keyword, Pageable pageable);

    @Modifying
    @Query("UPDATE Product p SET p.enabled = ?2 WHERE p.id = ?1")
    void updateProductEnabledStatus(int id, boolean status);
}
