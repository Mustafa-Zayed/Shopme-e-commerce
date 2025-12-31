package com.shopme.customer.general.service;

import com.shopme.customer.general.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CustomerService {
    private final CustomerRepository customerRepository;

    public boolean checkEmailUniqueness(String email) {
        return customerRepository.findByEmail(email) == null;
    }
}
