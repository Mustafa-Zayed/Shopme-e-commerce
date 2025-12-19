package com.shopme.admin.setting.state.repository;

import com.shopme.admin.setting.state.dto.StateDTO;
import com.shopme.common.entity.setting.country.Country;
import com.shopme.common.entity.setting.state.State;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface StateRepository extends CrudRepository<State, Integer>, PagingAndSortingRepository<State, Integer> {
    @Query("SELECT NEW com.shopme.admin.setting.state.dto.StateDTO(s.id, s.name) " +
            "FROM State s WHERE s.country = ?1 ORDER BY s.name ASC")
    List<StateDTO> findByCountryOrderByNameAsc(Country country);
}
