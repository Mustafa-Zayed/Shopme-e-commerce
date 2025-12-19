package com.shopme.admin.setting.state.repository;

import com.shopme.admin.setting.state.dto.StateDTO;
import com.shopme.common.entity.setting.country.Country;
import com.shopme.common.entity.setting.state.State;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
public class StateRepositoryTests {
    @Autowired
    private StateRepository stateRepository;
    @Autowired
    private TestEntityManager testEntityManager;

    @Test
    public void StateRepository_Save_ReturnSavedState() {
        Country country = testEntityManager.find(Country.class, 1);
        State cairo = new State("Cairo", country);
        System.out.println(cairo);
        State savedState = stateRepository.save(cairo);

        assertThat(savedState).isNotNull();
        assertThat(savedState.getId()).isGreaterThan(0);
    }

    @Test
    public void StateRepository_SaveAll_ReturnSavedStates() {
        Country country = testEntityManager.find(Country.class, 1);

        List<State> listStates = Arrays.asList(
                new State("Cairo", country),
                new State("Alexandria", country),
                new State("Giza", country),
                new State("Luxor", country),
                new State("Aswan", country),
                new State("Asyut", country),
                new State("Beni Suef", country),
                new State("Dakahlia", country),
                new State("Faiyum", country),
                new State("Gharbia", country),
                new State("Ismailia", country),
                new State("Minya", country),
                new State("Monastirat", country),
                new State("New Valley", country),
                new State("Qena", country),
                new State("Sohag", country),
                new State("Tripoli", country),
                new State("Wadi El Natrun", country),
                new State("Zagazig", country)
        );

        List<State> savedStates = (List<State>) stateRepository.saveAll(listStates);

        savedStates.forEach(System.out::println);

        assertThat(savedStates).hasSizeGreaterThan(0);
    }

    @Test
    public void StateRepository_FindByCountryOrderByNameAsc_ReturnStates() {
        Integer countryId = 14;
        Country country = testEntityManager.find(Country.class, countryId);
        List<StateDTO> states = stateRepository.findByCountryOrderByNameAsc(country);
        states.forEach(System.out::println);

        assertThat(states).hasSizeGreaterThan(0);
    }

    @Test
    public void StateRepository_FindAll_ReturnStates() {
        List<State> states = (List<State>) stateRepository.findAll();
        states.forEach(System.out::println);

        assertThat(states).hasSizeGreaterThan(0);
    }

    @Test
    public void StateRepository_FindById_ReturnState() {
        int id = 1;
        State state = stateRepository.findById(id).get();
        System.out.println(state);

        assertThat(state).isNotNull();
    }

    @Test
    public void StateRepository_UpdateState_ReturnState() {
        int id = 1;
        String name = "London";

        State state = stateRepository.findById(id).get();
        state.setName(name);

        State savedState = stateRepository.save(state);

        assertThat(savedState.getName()).isEqualTo(name);
    }

    @Test
    public void StateRepository_DeleteState_ReturnState() {
        int id = 19;
        State state = stateRepository.findById(id).get();
        stateRepository.delete(state);

        assertThat(stateRepository.findById(id)).isNotPresent();
    }
}
