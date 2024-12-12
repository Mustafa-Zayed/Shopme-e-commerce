package com.shopme.admin.user;

import com.shopme.common.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
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
        return "users";
    }

    @GetMapping("/users/new")
    public String newUser(Model model) {
        model.addAttribute("user", User.builder().enabled(true).build());
        model.addAttribute("listRoles", userService.listRoles());
        model.addAttribute("pageTitle", "Create New User");
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

        userService.save(user);
        if (user.getId() == 0) // new user
            redirectAttributes.addFlashAttribute("message", "User has been Created!");
        else
            redirectAttributes.addFlashAttribute("message", "User has been saved successfully!");
        return "redirect:/users";
    }

    @GetMapping("/users/edit/{id}")
    public String editUser(@PathVariable int id, Model model, RedirectAttributes redirectAttributes) {
        User user;
        try {
            user = userService.findById(id);
        } catch (UserNotFoundException ex) {
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
            return "redirect:/users";
        }
        model.addAttribute("user", user);
        model.addAttribute("listRoles", userService.listRoles());
        model.addAttribute("pageTitle", "Edit User(ID: " + id + ")");
        return "user_form";
    }

    @GetMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable int id, RedirectAttributes redirectAttributes) {
        try {
            userService.delete(id);
            redirectAttributes.addFlashAttribute("message", "User ID " + id + " has been deleted successfully!");
        } catch (UserNotFoundException ex) {
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
        }
        return "redirect:/users";
    }

    //    @GetMapping("/users/{id}/enabled")
    @GetMapping("/users/{id}/enabled/{status}")
    public String updateUserEnabledStatus(@PathVariable int id,
                                          @PathVariable(value = "status") boolean statusBefore,
//                                   @RequestParam(value = "status") boolean statusBefore,
                                          RedirectAttributes redirectAttributes) {
        try {
            userService.updateUserEnabledStatus(id, !statusBefore);
            if (statusBefore)
                redirectAttributes.addFlashAttribute("message", "User ID " + id + " has been disabled");
            else
                redirectAttributes.addFlashAttribute("message", "User ID " + id + " has been enabled");
        } catch (UserNotFoundException ex) {
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
        }
        return "redirect:/users";
    }

}
