package com.shopme.admin.setting.state.repository;

import com.shopme.common.entity.setting.country.Country;
import com.shopme.common.entity.setting.state.State;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface StateRepository extends CrudRepository<State, Integer>, PagingAndSortingRepository<State, Integer> {
    List<State> findByCountryOrderByNameAsc(Country country);
}
