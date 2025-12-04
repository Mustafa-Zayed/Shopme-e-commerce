package com.shopme.admin.setting.country.repository;

import com.shopme.common.entity.setting.country.Country;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
public class CountryRepositoryTests {

    @Autowired
    private CountryRepository countryRepository;

    @Test
    void CountryRepository_SaveCountry_ReturnCountry() {
        Country country = Country.builder().name("China").code("CN").build();
        Country savedCountry = countryRepository.save(country);

        assertThat(savedCountry).isNotNull();
        assertThat(savedCountry.getId()).isGreaterThan(0);
    }

    @Test
    void CountryRepository_FindAllByOrderByNameAsc_ReturnCountries() {
        List<Country> countries = countryRepository.findAllByOrderByNameAsc();
        countries.forEach(System.out::println);

        assertThat(countries).hasSizeGreaterThan(0);
    }

    @Test
    void CountryRepository_GetCountry_ReturnCountry() {
        int id = 1;
        Country country= countryRepository.findById(id).get();
        System.out.println(country);

        assertThat(country).isNotNull();
    }

    @Test
    void CountryRepository_UpdateCountry_ReturnCountry() {
        int id = 1;
        String name = "India";

        Country country= countryRepository.findById(id).get();
        country.setName(name);
        country.setCode("IN");

        Country savedCountry = countryRepository.save(country);

        assertThat(savedCountry.getName()).isEqualTo(name);
        assertThat(savedCountry.getCode()).isEqualTo("IN");
    }

    @Test
    void CountryRepository_DeleteCountry_ReturnCountry() {
        int id = 1;
        Country country= countryRepository.findById(id).get();
        countryRepository.delete(country);

        assertThat(countryRepository.findById(id)).isNotPresent();
    }
}
