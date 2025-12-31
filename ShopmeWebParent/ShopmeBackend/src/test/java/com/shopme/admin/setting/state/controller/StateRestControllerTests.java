package com.shopme.admin.setting.state.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopme.common.entity.setting.state.StateDTO;
import com.shopme.admin.setting.state.repository.StateRepository;
import com.shopme.common.entity.setting.country.Country;
import com.shopme.common.entity.setting.state.State;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * {@link WebMvcTest} does not load: DataSource, MySQL connection nor Repositories.
 * It only loads the specified controller and used for unit testing not integration testing.
 * <p>
 * If you want database access in tests, you must use:  @SpringBootTest + @AutoConfigureMockMvc
 */
@SpringBootTest
//@WebMvcTest(controllers = StateRestController.class)
@AutoConfigureMockMvc()
public class StateRestControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private StateRepository stateRepository;

    @Test
    @WithMockUser(username = "anyUser", password = "something", authorities = "Admin")
    public void StateRestController_ListAllStatesByCountry_ReturnAllStates() throws Exception {
        int countryId = 66;
        MvcResult result = mockMvc.perform(get("/settings/states/country/" + countryId)
                        // .queryParam("countryId", "66")
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
//        StateDTO[] states = objectMapper.readValue(jsonResponse, StateDTO[].class);
        List<StateDTO> states = objectMapper.readValue(jsonResponse, new TypeReference<>() {});

//         Arrays.stream(states).forEach(System.out::println);
        states.forEach(System.out::println);

        assertThat(states).hasSizeGreaterThan(0);
    }

    @Test
    @WithMockUser(username = "anyUser", password = "something", authorities = "Admin") // simulate a user logged in.
    public void StateRestController_SaveState_ReturnSavedState() throws Exception {
        Integer countryID = 66; // Egypt
        State state = State.builder()
                .name("Cairo")
                .country(Country.builder().id(countryID).build())
                .build();

        MvcResult result = mockMvc.perform(
                    post("/settings/states/save")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(state))
                    .with(csrf())) // CSRF token is required for any HTTP methods/requests that modify state (POST, PUT, DELETE, PATCH).
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        StateDTO savedState = objectMapper.readValue(jsonResponse, StateDTO.class);

        System.out.println(savedState);

        assertEquals(state.getName(), savedState.getName());
    }

    @Test
    @WithMockUser(username = "anyUser", password = "something", authorities = "Admin")
    public void StateRestController_UpdateState_ReturnUpdatedState() throws Exception {
        Integer id = 307;
        State state = stateRepository.findById(id).get();
        state.setName("Cairo");

        MvcResult result = mockMvc.perform(
                    post("/settings/states/save")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(state))
                    .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        StateDTO savedState = objectMapper.readValue(jsonResponse, StateDTO.class);

        System.out.println(savedState);

        assertEquals(state.getName(), savedState.getName());
    }

    @Test
    @WithMockUser(username = "anyUser", password = "something", authorities = "Admin")
    public void StateRestController_DeleteState_ReturnDeletedState() throws Exception {
        int StateId = 3;

        mockMvc.perform(
                    delete("/settings/states/delete/" + StateId)
                    .with(csrf())
                )
                .andExpect(status().isOk())
                .andDo(print());

        Optional<State> byId = stateRepository.findById(StateId);

        assertFalse(byId.isPresent());
        assertThat(byId).isNotPresent();
    }
}
