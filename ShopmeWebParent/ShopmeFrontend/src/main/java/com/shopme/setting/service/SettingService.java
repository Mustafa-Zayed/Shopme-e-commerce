package com.shopme.setting.service;

import com.shopme.setting.repository.SettingRepository;

import com.shopme.common.entity.SettingCategory;
import com.shopme.common.entity.setting.Setting;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class SettingService {
    private final SettingRepository settingRepository;

    public List<Setting> findGeneralAndCurrencySettings() {
        return settingRepository.findByTwoCategories(SettingCategory.GENERAL, SettingCategory.CURRENCY);
    }
}
