package com.shopme.admin.category.repository;

import com.shopme.common.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface CategoryRepository extends CrudRepository<Category, Integer>, PagingAndSortingRepository<Category, Integer>  {
    @Query("SELECT c FROM Category c WHERE CONCAT(c.id, ' ', c.alias, ' ', c.name) " +
            "LIKE %?1%")
    Page<Category> findAll(String keyword, Pageable pageable);

    Category findByName(String name);
    Category findByAlias(String alias);
}
