package com.shopme.admin.user.controller;

import com.shopme.admin.user.exception.UserNotFoundException;
import com.shopme.admin.user.service.UserService;
import com.shopme.admin.user.export.UserCsvExporter;
import com.shopme.admin.user.export.UserExcelExporter;
import com.shopme.admin.user.export.UserPDFExporter;
import com.shopme.admin.utility.paging_and_sorting.PagingAndSortingHelper;
import com.shopme.admin.utility.paging_and_sorting.PagingAndSortingParam;
import com.shopme.common.entity.User;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@Controller
public class UserController {
    private final UserService userService;

    @GetMapping("/users")
    public String listFirstPage() {
        return "redirect:/users/page/1?sortField=firstName&sortDir=asc";
        // return listByPageWithSorting(helper, 1); // not working, PagingAndSortingArgumentResolver expects URL has "?sortField=&sortDir="
    }

    @GetMapping("/users/page/{pageNumber}")
    public String listByPageWithSorting(
            @PagingAndSortingParam(listItemsName = "listUsers", pathURL = "users") PagingAndSortingHelper helper,
            @PathVariable int pageNumber) {
        userService.listByPageWithSorting(pageNumber, helper);
        return "users/users";
    }

    @GetMapping("/users/new")
    public String newUser(Model model) {
        model.addAttribute("user", User.builder().enabled(true).build());
        model.addAttribute("listRoles", userService.listRoles());
        model.addAttribute("pageTitle", "Create New User");
        return "users/user_form";
    }

    //RedirectAttributes is an interface used to pass attributes to a redirected request
    @PostMapping("/users/save")
    public String saveUser(@ModelAttribute User user,
                           @RequestPart(value = "userImage") MultipartFile multipart,
                           RedirectAttributes redirectAttributes) throws IOException {
        userService.save(user, multipart, redirectAttributes);

        // We use the first part of the email address to filter for the updated user after redirecting.
        String firstPartOfEmail = user.getEmail().split("@")[0];
        System.out.println("keyword: " + firstPartOfEmail);
        return "redirect:/users/page/1?sortField=id&sortDir=asc&keyword=" + firstPartOfEmail;
    }

    @GetMapping("/users/edit/{id}")
    public String editUser(@PathVariable int id, Model model, RedirectAttributes redirectAttributes) {
        User user;
        try {
            user = userService.findById(id);
        } catch (UserNotFoundException ex) {
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
            redirectAttributes.addFlashAttribute("resultClass", "danger");
            return "redirect:/users";
        }
        model.addAttribute("user", user);
        model.addAttribute("listRoles", userService.listRoles());
        model.addAttribute("pageTitle", "Edit User(ID: " + id + ")");
        return "users/user_form";
    }

    @GetMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable int id, RedirectAttributes redirectAttributes) {
        try {
            userService.delete(id);
            redirectAttributes.addFlashAttribute("message", "User ID " + id + " has been deleted successfully!");
        } catch (UserNotFoundException ex) {
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
            redirectAttributes.addFlashAttribute("resultClass", "danger");
        }
        return "redirect:/users";
    }

    //    @GetMapping("/users/{id}/enabled")
    @GetMapping("/users/{id}/enabled/{status}")
    public String updateUserEnabledStatus(@PathVariable int id,
                                          @PathVariable(value = "status") boolean statusBefore,
//                                   @RequestParam(value = "status") boolean statusBefore,
                                          RedirectAttributes redirectAttributes) {
        String firstPartOfEmail = "";
        try {
            userService.updateUserEnabledStatus(id, !statusBefore);
            User user = userService.findById(id);
            firstPartOfEmail = user.getEmail().split("@")[0];
            System.out.println("keyword: " + firstPartOfEmail);

            if (statusBefore)
                redirectAttributes.addFlashAttribute("message", "User ID " + id + " has been disabled");
            else
                redirectAttributes.addFlashAttribute("message", "User ID " + id + " has been enabled");
        } catch (UserNotFoundException ex) {
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
            redirectAttributes.addFlashAttribute("resultClass", "danger");
            return "redirect:/users";
        }
        return "redirect:/users/page/1?sortField=id&sortDir=asc&keyword=" + firstPartOfEmail;
//        return "redirect:/users";
    }

    @GetMapping("/users/export/csv")
    public void exportToCSV(HttpServletResponse response) throws IOException {
        UserCsvExporter csvExporter = new UserCsvExporter();
        List<User> userList = userService.listAll();
        csvExporter.export(userList, response);
    }

    @GetMapping("/users/export/excel")
    public void exportToExcel(HttpServletResponse response) throws IOException {
        UserExcelExporter excelExporter = new UserExcelExporter();
        List<User> userList = userService.listAll();
        excelExporter.export(userList, response);
    }

    @GetMapping("/users/export/pdf")
    public void exportToPDF(HttpServletResponse response) throws IOException {
        UserPDFExporter excelExporter = new UserPDFExporter();
        List<User> userList = userService.listAll();
        excelExporter.export(userList, response);
    }
}
