package com.shopme.admin.setting.repository;

import com.shopme.common.entity.Currency;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface CurrencyRepository extends CrudRepository<Currency, Integer>, PagingAndSortingRepository<Currency, Integer> {
}

