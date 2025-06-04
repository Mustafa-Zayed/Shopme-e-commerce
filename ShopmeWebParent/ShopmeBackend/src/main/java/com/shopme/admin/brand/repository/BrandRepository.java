package com.shopme.admin.brand.repository;

import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface BrandRepository extends CrudRepository<Brand, Integer>, PagingAndSortingRepository<Brand, Integer> {
    Integer countById(Integer id);

    Brand findByName(String name);

    @Override
    @Query("SELECT NEW Brand(b.id, b.name) FROM Brand b")
    List<Brand> findAll(Sort sort);

    @Query("SELECT b FROM Brand b WHERE b.name LIKE %?1%")
    Page<Brand> findAll(String keyword, Pageable pageable);

    @Query("SELECT b.categories FROM Brand b WHERE b.id = ?1")
    List<Category> findCategoriesByBrand(Integer brandId);
}
