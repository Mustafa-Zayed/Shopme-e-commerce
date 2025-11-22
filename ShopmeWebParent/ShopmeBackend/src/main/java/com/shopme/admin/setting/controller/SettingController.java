package com.shopme.admin.setting.controller;

import com.shopme.admin.setting.repository.CurrencyRepository;
import com.shopme.admin.setting.service.SettingService;
import com.shopme.admin.setting.utility.GeneralSettingBag;
import com.shopme.admin.utility.FileUploadUtil;
import com.shopme.common.entity.setting.Currency;
import com.shopme.common.entity.setting.Setting;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
@Controller
public class SettingController {
    private final SettingService settingService;
    private final CurrencyRepository currencyRepository;

    @GetMapping("/settings")
    public String listAll(Model model) {
        List<Setting> listSettings = settingService.findAll();
        List<Currency> listCurrencies = currencyRepository.findAllByOrderByNameAsc();

        model.addAttribute("listCurrencies", listCurrencies);

        // Add each setting separately to the model
        for (Setting setting : listSettings) {
            model.addAttribute(setting.getKey(), setting.getValue());
        }

        return "settings/settings";
    }

    /**
     * We are using HttpServletRequest to get all form data once in a loop instead
     *                of using @RequestParam for each form field separately
     */
    @PostMapping("/settings/general/save")
    public String saveGeneralSettings(RedirectAttributes redirectAttributes,
                                      @RequestPart("siteLogo") MultipartFile multipart,
                                      HttpServletRequest request) throws IOException {

        GeneralSettingBag settingBag = settingService.getGeneralSettingBagObject();

        updateSettingsFromForm(request, settingBag.findAll());

        updateSiteLogo(multipart, settingBag);
        updateCurrencySymbol(request, settingBag);

        settingService.saveAll(settingBag.findAll());

        String message ="General settings have been updated successfully!";
        redirectAttributes.addFlashAttribute("message", message);

        return "redirect:/settings";
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
}
