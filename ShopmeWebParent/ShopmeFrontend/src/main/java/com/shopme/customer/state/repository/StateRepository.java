package com.shopme.customer.state.repository;

import com.shopme.common.entity.setting.country.Country;
import com.shopme.common.entity.setting.state.State;
import com.shopme.common.entity.setting.state.StateDTO;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface StateRepository extends CrudRepository<State, Integer>, PagingAndSortingRepository<State, Integer> {
    @Query("SELECT NEW com.shopme.common.entity.setting.state.StateDTO(s.id, s.name) " +
            "FROM State s WHERE s.country = ?1 ORDER BY s.name ASC")
    List<StateDTO> findAllByCountryOrderByNameAsc(Country country);
}
