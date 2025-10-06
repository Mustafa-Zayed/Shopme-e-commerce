package com.shopme.admin;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @GetMapping("")
    public String viewHomePage() {
        return "index";
    }

    @GetMapping("/login")
    public String login(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated())
            return "login"; // normal login view

        return "redirect:/"; // or wherever the app should go
    }
}
