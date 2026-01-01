package com.shopme.admin.setting.general.controller;

import com.shopme.admin.setting.general.repository.CurrencyRepository;
import com.shopme.admin.setting.general.service.SettingService;
import com.shopme.admin.setting.utility.GeneralSettingBag;
import com.shopme.admin.utility.FileUploadUtil;
import com.shopme.common.entity.setting.general.Currency;
import com.shopme.common.entity.setting.general.Setting;
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

        settingService.saveGeneralSettings(multipart, request);

        String message ="General settings have been updated successfully!";
        redirectAttributes.addFlashAttribute("message", message);

        return "redirect:/settings";
    }
}
