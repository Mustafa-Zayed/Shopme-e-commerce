package com.shopme.admin.setting.general.service;

import com.shopme.admin.setting.general.repository.SettingRepository;
import com.shopme.admin.setting.utility.GeneralSettingBag;
import com.shopme.common.entity.setting.general.SettingCategory;
import com.shopme.common.entity.setting.general.Setting;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class SettingService {
    private final SettingRepository settingRepository;

    public List<Setting> findAll() {
        return (List<Setting>) settingRepository.findAll();
    }

    public void saveAll(Iterable<Setting> settings) {
        settingRepository.saveAll(settings);
    }

    public GeneralSettingBag getGeneralSettingBagObject() {
        List<Setting> settings = new ArrayList<>();

        List<Setting> generalSettings = settingRepository.findByCategory(SettingCategory.GENERAL);
        List<Setting> currencySettings = settingRepository.findByCategory(SettingCategory.CURRENCY);

        settings.addAll(generalSettings);
        settings.addAll(currencySettings);

        return new GeneralSettingBag(settings);
    }
}
