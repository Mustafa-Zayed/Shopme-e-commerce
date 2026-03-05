package com.shopme.customer.general.controller;

import com.shopme.common.entity.Customer;
import com.shopme.common.exception.CustomerNotFoundException;
import com.shopme.customer.general.service.CustomerService;
import com.shopme.utitlity.EmailConfig;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.UnsupportedEncodingException;

@RequiredArgsConstructor
@Controller
public class ForgotPasswordController {
    private final CustomerService customerService;

    @GetMapping("/forgot_password")
    public String showRequestForm() {
        return "customers/forgot_password/forgot_password_form";
    }

    @PostMapping("/forgot_password/send_reset_link")
    public String processForgotPassword(@RequestParam("email") String email,
                                        HttpServletRequest request,
                                        RedirectAttributes ra) {
        try {
            // generate random token
            String token = customerService.generateResetPasswordToken(email);
            String siteURL = EmailConfig.getSiteURL(request);
            String resetURL = siteURL + "/forgot_password/reset_password?token=" + token;
            customerService.sendResetPasswordLink(email, resetURL);
            return "redirect:/forgot_password?success";
        }
        catch (CustomerNotFoundException e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/forgot_password";
        }
        catch (MessagingException | UnsupportedEncodingException e) {
            ra.addFlashAttribute("error", "Could send email");
            return "redirect:/forgot_password";
        }
    }

    @GetMapping("/forgot_password/reset_password")
    public String showResetPasswordForm(@RequestParam(required = false) String token,
                                        HttpSession session,
                                        Model model) {
        if (token == null || token.isEmpty()) {
            session.setAttribute("result", "result");
            return "redirect:/forgot_password/reset_password_result?error=invalid_token";
        }
        Customer byResetPasswordToken = customerService.findByResetPasswordToken(token);
        if (byResetPasswordToken == null) {
            session.setAttribute("result", "result");
            return "redirect:/forgot_password/reset_password_result?error=invalid_token";
        }
        model.addAttribute("customer", byResetPasswordToken);
        return "customers/forgot_password/reset_form";
    }

    @PostMapping("/forgot_password/save")
    public String processResetPassword(@ModelAttribute("customer") Customer customer,
                                       HttpSession session) {
        if (customer == null)
            return "redirect:/forgot_password/reset_password_result?error";
        session.setAttribute("result", "result");
        customerService.updateCustomerNewPassword(customer);
        return "redirect:/forgot_password/reset_password_result?success";
    }

    // accessed only by redirection
    @GetMapping("/forgot_password/reset_password_result")
    public String showResultPage(HttpSession session) {
        String result = (String) session.getAttribute("result");
        if (result == null || result.isEmpty()) {
            return "redirect:/forgot_password";
        }
        session.removeAttribute("result");
        return "customers/forgot_password/reset_password_result";
    }
}
