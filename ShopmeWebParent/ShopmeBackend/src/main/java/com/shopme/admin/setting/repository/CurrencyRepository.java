package com.shopme.admin.setting.repository;

import com.shopme.common.entity.setting.Currency;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface CurrencyRepository extends CrudRepository<Currency, Integer>, PagingAndSortingRepository<Currency, Integer> {
    List<Currency> findAllByOrderByNameAsc();
}

