package com.shopme.admin.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class UserRestController {
    private final UserService userService;

    // Consumed by Ajax call in user_form.html
    @PostMapping("/users/check_email")
    public String checkDuplicateEmail(@RequestParam String email) {
        return userService.isEmailUnique(email) ? "OK" : "Duplicated";
    }
}
