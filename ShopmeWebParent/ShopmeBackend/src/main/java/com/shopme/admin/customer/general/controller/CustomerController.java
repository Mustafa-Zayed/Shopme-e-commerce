package com.shopme.admin.customer.general.controller;

import com.shopme.admin.customer.general.export.CustomerCsvExporter;
import com.shopme.admin.customer.general.service.CustomerService;
import com.shopme.common.entity.Customer;
import com.shopme.common.exception.CustomerNotFoundException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static com.shopme.admin.customer.general.service.CustomerService.CUSTOMERS_PER_PAGE;

@RequiredArgsConstructor
@Controller
public class CustomerController {
    private final CustomerService customerService;

    @GetMapping("/customers")
    public String listFirstPage(Model model) {
        return listByPageWithSorting(1, "firstName", "asc", "", model);
    }

    @GetMapping("/customers/page/{pageNumber}")
    public String listByPageWithSorting(
            @PathVariable int pageNumber,
            @RequestParam(name = "sortField", defaultValue = "firstName") String sortField,
            @RequestParam(name = "sortDir", defaultValue = "asc") String sortDir,
            @RequestParam(name = "keyword", defaultValue = "") String keyword,
            Model model) {

        Page<Customer> customersPage = customerService
                .listByPageWithSorting(pageNumber, sortField, sortDir, keyword);
        return addToModel(pageNumber, customersPage, sortField, sortDir, keyword, model);
    }

    private String addToModel(int pageNumber, Page<Customer> customersPage,
                              String sortField, String sortDir, String keyword, Model model) {
        List<Customer> listCustomers = customersPage.getContent();
        int totalPages = customersPage.getTotalPages();
        long totalItems = customersPage.getTotalElements();

        long startCount = ((long) (pageNumber - 1) * CUSTOMERS_PER_PAGE) + 1;
        long endCount = (startCount + CUSTOMERS_PER_PAGE) - 1;
        if (endCount > totalItems) endCount = totalItems;
        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);

        model.addAttribute("listCustomers", listCustomers);

        model.addAttribute("currentPage", pageNumber);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalItems", totalItems);

        String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";

        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", reverseSortDir);

        model.addAttribute("keyword", keyword);

        return "customers/customers";
    }

    @PostMapping("/customers/save")
    public String saveCustomer(@ModelAttribute Customer customer,
                               RedirectAttributes redirectAttributes) throws IOException {
        customerService.save(customer, redirectAttributes);

        // return to the saved customer in the listing page
        String keyword = customer.getFullName();

        String encodedKeyword = URLEncoder.encode(keyword, StandardCharsets.UTF_8);
        System.out.println("encodedKeyword: " + encodedKeyword);
        return "redirect:/customers/page/1?keyword=" + encodedKeyword;
    }

    @GetMapping("/customers/edit/{id}")
    public String editCustomer(@PathVariable int id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Customer customer = customerService.findById(id);

            model.addAttribute("customer", customer);
            model.addAttribute("countries", customerService.findAllCountries());
            model.addAttribute("pageTitle", "Edit Customer (ID: " + id + ")");

            return "customers/customer_form";
        } catch (CustomerNotFoundException ex) {
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
            redirectAttributes.addFlashAttribute("resultClass", "danger");
            return "redirect:/customers";
        }
    }

    @GetMapping("/customers/delete/{id}")
    public String deleteCustomer(@PathVariable int id, RedirectAttributes redirectAttributes) {
        try {
            customerService.delete(id);
            redirectAttributes.addFlashAttribute("message", "Customer ID " + id + " has been deleted successfully!");
        } catch (CustomerNotFoundException ex) {
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
            redirectAttributes.addFlashAttribute("resultClass", "danger");
        }
        return "redirect:/customers";
    }

    @GetMapping("/customers/{id}/enabled/{status}")
    public String updateCustomerEnabledStatus(@PathVariable int id,
                                              @PathVariable(value = "status") boolean statusBefore,
                                              RedirectAttributes redirectAttributes) {
        String keyword;
        try {
            customerService.updateCustomerEnabledStatus(id, !statusBefore);
            Customer customer = customerService.findById(id);
            keyword = customer.getFullName();

            if (statusBefore)
                redirectAttributes.addFlashAttribute("message", "Customer ID " + id + " has been disabled");
            else
                redirectAttributes.addFlashAttribute("message", "Customer ID " + id + " has been enabled");
        } catch (CustomerNotFoundException ex) {
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
            redirectAttributes.addFlashAttribute("resultClass", "danger");
            return "redirect:/customers";
        }
        // URLs with spaces are invalid and can lead to unexpected behavior. Even though %20 is
        // the encoded form of a space, Spring's redirect handling might not properly encode or
        // interpret the URL, causing issues with flash attributes.
        // URLs must be properly encoded (no spaces allowed)
        String encodedKeyword = URLEncoder.encode(keyword, StandardCharsets.UTF_8);
        System.out.println("encodedKeyword: " + encodedKeyword);

        return "redirect:/customers/page/1?keyword=" + encodedKeyword;
        // return "redirect:/customers";
    }

    @GetMapping("/customers/details/{id}")
    public String viewCustomerDetails(@PathVariable int id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Customer customer = customerService.findById(id);
            model.addAttribute("customer", customer);
            return "customers/customer_details_modal";
        } catch (CustomerNotFoundException ex) {
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
            redirectAttributes.addFlashAttribute("resultClass", "danger");
            return "redirect:/customers";
        }
    }

    @GetMapping("/customers/export/csv")
    public void exportToCSV(HttpServletResponse response) throws IOException {
        CustomerCsvExporter csvExporter = new CustomerCsvExporter();
        List<Customer> customerList = customerService.listAll();
        csvExporter.export(customerList, response);
    }
}
