package com.shopme.admin.setting.general.repository;

import com.shopme.common.entity.setting.general.Setting;
import com.shopme.common.entity.setting.general.SettingCategory;
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
    private SettingRepository settingRepository;

    @Test
    public void SettingRepository_Save_ReturnSavedGeneralSetting() {
        Setting siteName = new Setting("SITE_NAME", "Shopme", SettingCategory.GENERAL);
        Setting savedSetting = settingRepository.save(siteName);
        System.out.println(savedSetting);
        assertThat(savedSetting).isNotNull();
    }

    @Test
    public void SettingRepository_SaveAll_ReturnSavedGeneralSettings() {
        Setting siteLogo = new Setting("SITE_LOGO", "Shopme.png", SettingCategory.GENERAL);
        Setting copyright = new Setting("COPYRIGHT", "Copyright © 2025 Shopme Ltd.", SettingCategory.GENERAL);
        Iterable<Setting> settings = settingRepository.saveAll(List.of(siteLogo, copyright));
        assertThat(settings).isNotNull();
    }

    @Test
    public void SettingRepository_FindAll_ReturnAllSettings() {
        Iterable<Setting> settings = settingRepository.findAll();
        settings.forEach(System.out::println);
        assertThat(settings).isNotNull();
        assertThat(settings).size().isGreaterThan(0);
    }

    @Test
    public void SettingRepository_SaveAll_ReturnSavedCurrencySettings() {
        Setting currencyId = new Setting("CURRENCY_ID", "1", SettingCategory.CURRENCY);
        Setting symbol = new Setting("CURRENCY_SYMBOL", "$", SettingCategory.CURRENCY);
        Setting symbolPosition = new Setting("CURRENCY_SYMBOL_POSITION", "before", SettingCategory.CURRENCY);
        Setting decimalPointType = new Setting("DECIMAL_POINT_TYPE", "POINT", SettingCategory.CURRENCY);
        Setting decimalDigits = new Setting("DECIMAL_DIGITS", "2", SettingCategory.CURRENCY);
        Setting thousandPointType = new Setting("THOUSANDS_POINT_TYPE", "COMMA", SettingCategory.CURRENCY);

        Iterable<Setting> settings = settingRepository.saveAll(
                List.of(currencyId, symbol, symbolPosition, decimalPointType, decimalDigits, thousandPointType)
        );
        settings.forEach(System.out::println);
        assertThat(settings).isNotNull();
    }

    @Test
    public void SettingRepository_FindByCategory_ReturnAllSettingsByCategory() {
        List<Setting> settings = settingRepository.findByCategory(SettingCategory.GENERAL);
        settings.forEach(System.out::println);
        assertThat(settings).isNotNull();
    }
}
