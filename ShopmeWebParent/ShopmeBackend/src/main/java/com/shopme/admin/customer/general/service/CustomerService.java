package com.shopme.admin.customer.general.service;

import com.shopme.admin.customer.general.repository.CustomerRepository;
import com.shopme.admin.setting.country.service.CountryService;
import com.shopme.admin.utility.paging_and_sorting.PagingAndSortingHelper;
import com.shopme.common.entity.Customer;
import com.shopme.common.entity.setting.country.Country;
import com.shopme.common.exception.CustomerNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.Date;
import java.util.List;

@RequiredArgsConstructor
@Service
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final CountryService countryService;
    private final PasswordEncoder passwordEncoder;

    public static final int CUSTOMERS_PER_PAGE = 10;

    public List<Customer> listAll() {
        return (List<Customer>) customerRepository.findAll(Sort.by("id").ascending());
    }

    public void listByPageWithSorting(int pageNumber, PagingAndSortingHelper helper) {
        helper.listByPageWithSorting(pageNumber, CUSTOMERS_PER_PAGE, customerRepository);
    }

    public Customer findById(int id) throws CustomerNotFoundException {
        return customerRepository.findById(id).orElseThrow(
                () -> new CustomerNotFoundException("Could not find any customer with ID: " + id)
        );
    }

    public List<Country> findAllCountries() {
        return countryService.findAll();
    }

    public Customer save(Customer customer,
                        RedirectAttributes redirectAttributes) throws IOException {
        Customer customerInDB = customerRepository.findById(customer.getId()).get();

        if (customer.getPassword().isEmpty()) {
            customer.setPassword(customerInDB.getPassword());
        } else
            encodePassword(customer);

        customer.setCreatedTime(customerInDB.getCreatedTime());
        customer.setEnabled(customerInDB.isEnabled());
        customer.setVerificationCode(customerInDB.getVerificationCode());
        customer.setAuthenticationType(customerInDB.getAuthenticationType());

        Customer savedCustomer = customerRepository.save(customer);

        String message = "Customer has been updated successfully!";
        redirectAttributes.addFlashAttribute("message", message);
        return savedCustomer;
    }

    private void encodePassword(Customer customer) {
        String encodedPassword = passwordEncoder.encode(customer.getPassword());
        customer.setPassword(encodedPassword);
    }

    public boolean checkEmailUniqueness(String email, Integer id) {
        Customer byEmail = customerRepository.findByEmail(email);

        if (byEmail == null)
            return true;

        return byEmail.getId().equals(id);
    }

    public boolean checkFullNameUniqueness(String fullName, Integer id) {
        Customer byFullName = customerRepository.findByFullName(fullName);

        if (byFullName == null)
            return true;

        return byFullName.getId().equals(id);
    }

    public boolean checkPhoneNumberUniqueness(String phoneNumber, Integer id) {
        Customer byPhoneNumber = customerRepository.findByPhoneNumber(phoneNumber);

        if (byPhoneNumber == null)
            return true;

        return byPhoneNumber.getId().equals(id);
    }

    @Transactional
    public void updateCustomerEnabledStatus(int id, boolean status) {
        customerRepository.updateCustomerEnabledStatus(id, status);
    }

    public void delete(int id) throws CustomerNotFoundException {
        Integer count = customerRepository.countById(id);
        if (count == 0)
            throw new CustomerNotFoundException("Could not find any customer with ID: " + id);

        customerRepository.deleteById(id);
    }
}
