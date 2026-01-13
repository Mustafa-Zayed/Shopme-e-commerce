package com.shopme.admin.customer.state.service;

import com.shopme.admin.customer.state.repository.StateRepository;
import com.shopme.common.entity.setting.country.Country;
import com.shopme.common.entity.setting.state.StateDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service("customerStateService") // to avoid conflict with StateService in setting package
public class StateService {
    private final StateRepository stateRepository;

    public List<StateDTO> findAllByCountry(Country country) {
        return stateRepository.findAllByCountryOrderByNameAsc(country);
    }

}
