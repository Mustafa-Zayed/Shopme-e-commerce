package com.shopme.admin.brand.repository;

import com.shopme.admin.utility.paging_and_sorting.repository.SearchRepository;
import com.shopme.common.entity.Brand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BrandRepository extends SearchRepository<Brand, Integer> {
    Integer countById(Integer id);

    Brand findByName(String name);

    @Override
    @Query("SELECT NEW Brand(b.id, b.name) FROM Brand b")
    List<Brand> findAll(Sort sort);

    @Query("SELECT b FROM Brand b WHERE b.name LIKE %?1%")
    Page<Brand> findAll(String keyword, Pageable pageable);
}
