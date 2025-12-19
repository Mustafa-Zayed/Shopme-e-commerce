package com.shopme.admin.setting.state.controller;

import com.shopme.admin.setting.state.dto.StateDTO;
import com.shopme.admin.setting.state.service.StateService;
import com.shopme.common.entity.setting.country.Country;
import com.shopme.common.entity.setting.state.State;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class StateRestController {
    private final StateService stateService;

    @GetMapping("/settings/states/country/{countryId}")
    public List<StateDTO> listAllStatesByCountry(@PathVariable("countryId") Integer countryId) {
        Country country = Country.builder().id(countryId).build();
        return stateService.findAll(country);
    }

    @PostMapping("/settings/states/save")
    public State saveCountry(@RequestBody State state) {
        return stateService.save(state);
    }

    @DeleteMapping("/settings/states/delete/{id}")
    public void deleteCountry(@PathVariable("id") Integer id) {
        stateService.delete(id);
    }
}
