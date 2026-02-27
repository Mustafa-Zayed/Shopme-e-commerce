package com.shopme.customer.general.controller;

import com.shopme.common.entity.Customer;
import com.shopme.common.entity.setting.country.Country;
import com.shopme.customer.country.service.CountryService;
import com.shopme.customer.general.service.CustomerService;
import com.shopme.security.ShopmeCustomerUserDetails;
import com.shopme.security.oauth2.CustomerOAuth2User;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@RequiredArgsConstructor
@Controller
public class AccountController {
    private final CustomerService customerService;
    private final CountryService countryService;

    @GetMapping("/account")
    public String viewAccountDetails(@AuthenticationPrincipal Object principal,
                                     Model model) {
//        // We can check the type of the principal using HttpServletRequest instead of @AuthenticationPrincipal
//        String principalType = request.getUserPrincipal().getClass().getName(); // e.g. UsernamePasswordAuthenticationToken, RememberMeAuthenticationToken or OAuth2UserAuthenticationToken
//        System.out.println("userPrincipal: " + principalType);
        String customerEmail = "";
        if (principal instanceof ShopmeCustomerUserDetails shopmeCustomerUserDetails) { // if the user is not logged in via OAuth2 i.e. a database user
            customerEmail = shopmeCustomerUserDetails.getUsername();
        } else if (principal instanceof CustomerOAuth2User customerOAuth2User) {
            customerEmail = customerOAuth2User.getEmail();
        }

        Customer customer = customerService.findByEmail(customerEmail);
        List<Country> countries = countryService.findAll();

        model.addAttribute("customer", customer);
        model.addAttribute("countries", countries);
        return "customers/account/account_form";
    }

    @PostMapping("/account/update")
    public String updateAccountDetails(@ModelAttribute Customer customer,
                                       @AuthenticationPrincipal Object principal,
                                       RedirectAttributes redirectAttributes) {
        customerService.updateCustomerAccountDetails(customer, redirectAttributes);
        updateNameForAuthCustomer(principal, customer.getFirstName(), customer.getLastName());
        return "redirect:/account";
    }

    // Update the name of the authenticated customer using the principal to update the name in the navbar
    private void updateNameForAuthCustomer(Object principal, String firstName, String lastName) {
        if (principal instanceof CustomerOAuth2User customerOAuth2User) { // OAuth2 user
            String fullName = firstName + " " + lastName;
            customerOAuth2User.setFullName(fullName);
        } else if (principal instanceof ShopmeCustomerUserDetails shopmeCustomerUserDetails) {
            shopmeCustomerUserDetails.setFirstName(firstName);
            shopmeCustomerUserDetails.setLastName(lastName);
        }
    }
}
