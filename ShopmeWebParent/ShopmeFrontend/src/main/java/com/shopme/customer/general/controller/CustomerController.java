package com.shopme.customer.general.controller;

import com.shopme.common.entity.Customer;
import com.shopme.common.entity.setting.country.Country;
import com.shopme.customer.country.service.CountryService;
import com.shopme.customer.general.service.CustomerService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.UnsupportedEncodingException;
import java.util.List;

@RequiredArgsConstructor
@Controller
public class CustomerController {
    private final CountryService countryService;
    private final CustomerService customerService;

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        List<Country> countries = countryService.findAll();

        model.addAttribute("pageTitle", "Customer Registration");
        model.addAttribute("customer", new Customer());
        model.addAttribute("countries", countries);

        return "customers/register/register_form";
    }

    @PostMapping("/register/save")
    public String saveCustomerForm(@ModelAttribute("customer") Customer customer,
                                   HttpServletRequest request,
                                   HttpSession session,
                                   RedirectAttributes ra) throws MessagingException, UnsupportedEncodingException {
        customerService.saveCustomer(customer, request);

        session.setAttribute("REGISTRATION_SUCCESS", true); //ra.addFlashAttribute("success", true);

        ra.addFlashAttribute("pageTitle", "Registration Succeeded!");
        return "redirect:/register/success";
    }

    // can be accessed only after successful registration only by redirection
    @GetMapping("/register/success")
    public String showRegisterSuccess(HttpSession session) {
        Boolean success = (Boolean) session.getAttribute("REGISTRATION_SUCCESS");

        if (success == null || !success) {
            return "redirect:/register";
        }

        // Remove it so refresh won't work again
        session.removeAttribute("REGISTRATION_SUCCESS");

        return "customers/register/register_success";
    }
}
