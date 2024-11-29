package com.shopme.admin.user;

import com.shopme.common.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@RequiredArgsConstructor
@Controller
public class UserController {
    private final UserService userService;

    @GetMapping("/users")
    public String listAll(Model model) {
        List<User> listUsers = userService.listAll();
        model.addAttribute("listUsers", listUsers);
        // get the message from the model of pre-redirect link and add to the model of users.html
        model.addAttribute("message", model.getAttribute("message"));
        return "users";
    }

    @GetMapping("/users/new")
    public String newUser(Model model) {
        model.addAttribute("user", User.builder().enabled(true).build());
        model.addAttribute("listRoles", userService.listRoles());
        return "user_form";
    }

    //RedirectAttributes is an interface used to pass attributes to a redirected request
    @PostMapping("/users/save")
    public String saveUser(@ModelAttribute User user, RedirectAttributes redirectAttributes, Model model) {
//        // Server-Side Rendering Approach
//        if (!userService.isEmailUnique(user.getEmail())) {
//            model.addAttribute("emailError", "Email is already in use!");
//            model.addAttribute("listRoles", userService.listRoles());
//            return "user_form";
//        }
        User saved = userService.save(user);
        redirectAttributes.addFlashAttribute("message", "User has been Created!");
        return "redirect:/users";
    }

}
