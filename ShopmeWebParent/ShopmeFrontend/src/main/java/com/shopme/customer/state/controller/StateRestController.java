package com.shopme.customer.state.controller;

import com.shopme.common.entity.setting.country.Country;
import com.shopme.common.entity.setting.state.StateDTO;
import com.shopme.customer.state.service.StateService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class StateRestController {
    private final StateService stateService;

    @GetMapping("/register/states_by_country/{countryId}")
    public List<StateDTO> listAllStatesByCountry(@PathVariable("countryId") Integer countryId) {
        Country country = Country.builder().id(countryId).build();
        return stateService.findAllByCountry(country);
    }

}
