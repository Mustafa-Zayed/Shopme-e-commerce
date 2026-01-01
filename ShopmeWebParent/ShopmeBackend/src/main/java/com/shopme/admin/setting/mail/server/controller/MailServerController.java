package com.shopme.admin.setting.mail.server.controller;

import com.shopme.admin.setting.general.service.SettingService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class MailServerController {

    private final SettingService settingService;

    @PostMapping("/settings/mailServer/save")
    public String saveMailServerSettings(RedirectAttributes redirectAttributes,
                                      HttpServletRequest request) {

        settingService.saveMailServerSettings(request);

        String message ="Mail Server settings have been updated successfully!";
        redirectAttributes.addFlashAttribute("message", message);

        return "redirect:/settings#mailServer";
    }
}
