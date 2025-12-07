package com.shopme.admin.setting.country.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopme.admin.setting.country.repository.CountryRepository;
import com.shopme.common.entity.setting.country.Country;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * {@link WebMvcTest} does not load: DataSource, MySQL connection nor Repositories.
 * It only loads the specified controller and used for unit testing not integration testing.
 * <p>
 * If you want database access in tests, you must use:  @SpringBootTest + @AutoConfigureMockMvc
 */
@SpringBootTest
//@WebMvcTest(controllers = CountryRestController.class)
@AutoConfigureMockMvc()
public class CountryRestControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CountryRepository countryRepository;

    @Test
    @WithMockUser(username = "anyUser", password = "something", roles = "ADMIN")
    public void CountryRestController_ListAllCountries_ReturnAllCountries() throws Exception {

        MvcResult result = mockMvc.perform(get("/countries"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                // .andDo(print())
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        // Country[] countries = objectMapper.readValue(jsonResponse, Country[].class);
        List<Country> countries = objectMapper.readValue(jsonResponse, new TypeReference<>() {
        });

        // Arrays.stream(countries).forEach(System.out::println);
        System.out.println(countries);

        assertThat(countries).hasSizeGreaterThan(0);
    }

    @Test
    @WithMockUser(username = "anyUser", password = "something", roles = "ADMIN") // simulate a user logged in.
    public void CountryRestController_SaveCountry_ReturnSavedCountry() throws Exception {
        Country country = Country.builder()
                .name("Spain")
                .code("SP")
                .build();

        MvcResult result = mockMvc.perform(
                    post("/countries/save")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(country))
                    .with(csrf())) // CSRF token is required for any HTTP methods/requests that modify state (POST, PUT, DELETE, PATCH).
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        Country savedCountry = objectMapper.readValue(jsonResponse, Country.class);

        System.out.println(savedCountry);

        assertEquals(country.getName(), savedCountry.getName());
        assertEquals(country.getCode(), savedCountry.getCode());
    }

    @Test
    @WithMockUser(username = "anyUser", password = "something", roles = "ADMIN")
    public void CountryRestController_UpdateCountry_ReturnUpdatedCountry() throws Exception {
        Integer id = 5;
        Country country = Country.builder()
                .id(id)
                .name("Germany")
                .code("DE")
                .build();

        MvcResult result = mockMvc.perform(
                    post("/countries/save")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(country))
                    .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        Country savedCountry = objectMapper.readValue(jsonResponse, Country.class);

        System.out.println(savedCountry);

        assertEquals(country.getName(), savedCountry.getName());
        assertEquals(country.getCode(), savedCountry.getCode());
    }

    @Test
    @WithMockUser(username = "anyUser", password = "something", roles = "ADMIN")
    public void CountryRestController_DeleteCountry_ReturnDeletedCountry() throws Exception {
        int id = 3;

        mockMvc.perform(
                    delete("/countries/delete/" + id)
                    .with(csrf())
                )
                .andExpect(status().isOk())
                .andDo(print());

        Optional<Country> byId = countryRepository.findById(id);

        assertFalse(byId.isPresent());
        assertThat(byId).isNotPresent();
    }
}
