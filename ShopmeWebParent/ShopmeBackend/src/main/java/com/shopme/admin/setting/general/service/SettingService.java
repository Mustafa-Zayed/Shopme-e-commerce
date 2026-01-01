package com.shopme.admin.setting.general.service;

import com.shopme.admin.setting.general.repository.CurrencyRepository;
import com.shopme.admin.setting.general.repository.SettingRepository;
import com.shopme.admin.setting.utility.GeneralSettingBag;
import com.shopme.admin.utility.FileUploadUtil;
import com.shopme.common.entity.setting.general.Currency;
import com.shopme.common.entity.setting.general.SettingCategory;
import com.shopme.common.entity.setting.general.Setting;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class SettingService {
    private final SettingRepository settingRepository;
    private final CurrencyRepository currencyRepository;

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

    public void saveGeneralSettings(MultipartFile multipart, HttpServletRequest request) throws IOException {
        GeneralSettingBag settingBag = getGeneralSettingBagObject();

        updateSettingsFromForm(request, settingBag.findAll());

        updateSiteLogo(multipart, settingBag);
        updateCurrencySymbol(request, settingBag);

        saveAll(settingBag.findAll());
    }

    private void updateSettingsFromForm(HttpServletRequest request, List<Setting> settings) {
        for (Setting setting : settings) {
            String value = request.getParameter(setting.getKey());
            if (value != null) {
                setting.setValue(value);
            }
        }
    }

    private void updateCurrencySymbol(HttpServletRequest request, GeneralSettingBag generalSettingBag) {
        int currencyId = Integer.parseInt(request.getParameter("CURRENCY_ID"));
        Optional<Currency> optionalCurrency = currencyRepository.findById(currencyId);
        if (optionalCurrency.isPresent()) {
            String currencySymbol = optionalCurrency.get().getSymbol();
            generalSettingBag.updateCurrencySymbol(currencySymbol);
        }

    }

    private void updateSiteLogo(MultipartFile multipart, GeneralSettingBag generalSettingBag) throws IOException {
        if (multipart != null && !multipart.isEmpty()) {
            String originalFilename = StringUtils.cleanPath(Objects.requireNonNull(multipart.getOriginalFilename()));
            String value = "/site-logo/" + originalFilename;

            generalSettingBag.updateSiteLogo(value);

            String uploadDir = "../site-logo/";
            FileUploadUtil.saveFile(uploadDir, originalFilename, multipart);
        }
    }

    public List<Setting> getMailServerSettings() {
        return settingRepository.findByCategory(SettingCategory.MAIL_SERVER);
    }

    public void saveMailServerSettings(HttpServletRequest request) {

        List<Setting> mailServerSettings = getMailServerSettings();

        updateSettingsFromForm(request, mailServerSettings);

        saveAll(mailServerSettings);
    }

    public List<Setting> getMailTemplatesSettings() {
        return settingRepository.findByCategory(SettingCategory.MAIL_TEMPLATES);
    }

    public void saveMailTemplatesSettings(HttpServletRequest request) {

        List<Setting> mailServerSettings = getMailTemplatesSettings();

        updateSettingsFromForm(request, mailServerSettings);

        saveAll(mailServerSettings);
    }
}
