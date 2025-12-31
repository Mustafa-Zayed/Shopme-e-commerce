package com.shopme.customer.country.repository;

import com.shopme.common.entity.setting.country.Country;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface CountryRepository extends CrudRepository<Country, Integer>, PagingAndSortingRepository<Country, Integer> {
    List<Country> findAllByOrderByNameAsc();
}
