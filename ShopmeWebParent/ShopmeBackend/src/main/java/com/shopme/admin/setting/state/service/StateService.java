package com.shopme.admin.setting.state.service;

import com.shopme.common.entity.setting.state.StateDTO;
import com.shopme.admin.setting.state.repository.StateRepository;
import com.shopme.common.entity.setting.country.Country;
import com.shopme.common.entity.setting.state.State;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class StateService {
    private final StateRepository stateRepository;

    public List<StateDTO> findAll(Country country) {
        return stateRepository.findByCountryOrderByNameAsc(country);
    }

    public State save(State state) {
        return stateRepository.save(state);
    }

    public void delete(Integer id) {
        stateRepository.deleteById(id);
    }
}
