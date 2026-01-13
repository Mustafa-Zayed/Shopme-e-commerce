package com.shopme.admin.customer.state.controller;

import com.shopme.admin.customer.state.service.StateService;
import com.shopme.common.entity.setting.country.Country;
import com.shopme.common.entity.setting.state.StateDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController("customerStateRestController") // to avoid conflict with StateRestController in setting package
public class StateRestController {
    private final StateService stateService;

    @GetMapping("/customers/states_by_country/{countryId}")
    public List<StateDTO> listAllStatesByCountry(@PathVariable("countryId") Integer countryId) {
        Country country = Country.builder().id(countryId).build();
        return stateService.findAllByCountry(country);
    }

}
