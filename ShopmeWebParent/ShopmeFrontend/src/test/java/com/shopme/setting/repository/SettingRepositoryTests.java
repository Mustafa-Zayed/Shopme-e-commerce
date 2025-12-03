package com.shopme.setting.repository;

import com.shopme.common.entity.SettingCategory;
import com.shopme.common.entity.setting.Setting;
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
public class SettingRepositoryTests {

    @Autowired
    private SettingRepository SettingRepository;

    @Test
    void SettingRepository_FindByTwoCategories_ReturnSettings() {
        List<Setting> settings = SettingRepository.findByTwoCategories(SettingCategory.GENERAL, SettingCategory.CURRENCY);
        settings.forEach(System.out::println);

        assertThat(settings).hasSizeGreaterThan(0);
    }
}
