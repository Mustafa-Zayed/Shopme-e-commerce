package com.shopme.customer.general.controller;

import com.shopme.customer.general.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class CustomerRestController {
    private final CustomerService customerService;

    @PostMapping("/register/check_email_uniqueness")
    public boolean checkEmailUniqueness(@RequestParam String email) {
        return customerService.checkEmailUniqueness(email);
    }
}
