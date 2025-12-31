package com.shopme.customer.state.service;

import com.shopme.common.entity.setting.country.Country;
import com.shopme.common.entity.setting.state.StateDTO;
import com.shopme.customer.state.repository.StateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class StateService {
    private final StateRepository stateRepository;

    public List<StateDTO> findAllByCountry(Country country) {
        return stateRepository.findAllByCountryOrderByNameAsc(country);
    }

}
