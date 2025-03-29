package com.shopme.admin.user;

import com.shopme.admin.security.ShopmeUserDetails;
import com.shopme.common.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

@RequiredArgsConstructor
@Controller
public class AccountController {
    private final UserService userService;

    @GetMapping("/account")
    public String accountDetails(@AuthenticationPrincipal ShopmeUserDetails loggedUser,
                                 Model model) {
        String userEmail = loggedUser.getUsername();
        User user = userService.findByEmail(userEmail);
        model.addAttribute("user", user);
        return "account_form";
    }

    @PostMapping("/account/update")
    public String updateAccount(@ModelAttribute User user,
                                @RequestPart(value = "userImage") MultipartFile multipart,
                                @AuthenticationPrincipal ShopmeUserDetails loggedUser,
                                RedirectAttributes redirectAttributes) throws IOException {
        userService.saveUserAndImage(user, multipart, redirectAttributes);
        // update the user details in the session, so that they are updated in the UI
        loggedUser.setFirstName(user.getFirstName());
        loggedUser.setLastName(user.getLastName());
        return "redirect:/account";
    }
}
