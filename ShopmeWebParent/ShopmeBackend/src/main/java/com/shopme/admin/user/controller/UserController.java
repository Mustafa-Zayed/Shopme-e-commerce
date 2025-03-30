package com.shopme.admin.user.controller;

import com.shopme.admin.user.exception.UserNotFoundException;
import com.shopme.admin.user.service.UserService;
import com.shopme.admin.user.export.UserCsvExporter;
import com.shopme.admin.user.export.UserExcelExporter;
import com.shopme.admin.user.export.UserPDFExporter;
import com.shopme.common.entity.User;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

import static com.shopme.admin.user.service.UserService.USERS_PER_PAGE;

@RequiredArgsConstructor
@Controller
public class UserController {
    private final UserService userService;

    @GetMapping("/users")
    public String listFirstPage(Model model) {
        return listByPageWithSorting(1, "firstName", "asc", "", model);
    }

//    @GetMapping("/users/page/{pageNumber}")
//    public String listByPage(@PathVariable int pageNumber, Model model) {
//        Page<User> usersPage = userService.listByPage(pageNumber);
//        return addToModel(pageNumber, usersPage, model, "id", "asc");
//    }

    @GetMapping("/users/page/{pageNumber}")
    public String listByPageWithSorting(
            @PathVariable int pageNumber,
            @RequestParam(name = "sortField", defaultValue = "firstName") String sortField,
            @RequestParam(name = "sortDir", defaultValue = "asc") String sortDir,
            @RequestParam(name = "keyword", defaultValue = "") String keyword,
            Model model) {
        Page<User> usersPage = userService.listByPageWithSorting(pageNumber, sortField, sortDir, keyword);
        return addToModel(pageNumber, usersPage, sortField, sortDir, keyword, model);
    }

    private String addToModel(@PathVariable int pageNumber, Page<User> usersPage,
                              String sortField, String sortDir, String keyword, Model model) {
        List<User> listUsers = usersPage.getContent();
        int totalPages = usersPage.getTotalPages();
        long totalItems = usersPage.getTotalElements();

        long startCount = ((long) (pageNumber - 1) * USERS_PER_PAGE) + 1;
        long endCount = (startCount + USERS_PER_PAGE) - 1;
        if (endCount > totalItems) endCount = totalItems;
        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);

        model.addAttribute("listUsers", listUsers);
        model.addAttribute("currentPage", pageNumber);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalItems", totalItems);

        String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";

        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", reverseSortDir);

        model.addAttribute("keyword", keyword);

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
        userService.saveUserAndImage(user, multipart, redirectAttributes);

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
