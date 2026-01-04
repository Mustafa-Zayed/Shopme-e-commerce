package com.shopme.setting.service;

import com.shopme.common.entity.setting.general.utility.EmailSettingBag;
import com.shopme.setting.repository.SettingRepository;

import com.shopme.common.entity.setting.general.SettingCategory;
import com.shopme.common.entity.setting.general.Setting;
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

    public EmailSettingBag getEmailSettingsBag() {
        List<Setting> settings = settingRepository.findByTwoCategories(SettingCategory.MAIL_SERVER, SettingCategory.MAIL_TEMPLATES);
        return new EmailSettingBag(settings);
    }
}
