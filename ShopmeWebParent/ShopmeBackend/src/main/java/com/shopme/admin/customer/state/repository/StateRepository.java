package com.shopme.admin.customer.state.repository;

import com.shopme.common.entity.setting.country.Country;
import com.shopme.common.entity.setting.state.State;
import com.shopme.common.entity.setting.state.StateDTO;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("customerStateRepository") // to avoid conflict with StateRepository in setting package
public interface StateRepository extends CrudRepository<State, Integer>, PagingAndSortingRepository<State, Integer> {
    @Query("SELECT NEW com.shopme.common.entity.setting.state.StateDTO(s.id, s.name) " +
            "FROM State s WHERE s.country = ?1 ORDER BY s.name ASC")
    List<StateDTO> findAllByCountryOrderByNameAsc(Country country);
}
