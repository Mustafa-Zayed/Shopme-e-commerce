package com.shopme.security;

import com.shopme.common.entity.Customer;
import com.shopme.customer.general.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ShopmeCustomerUserDetailsService implements UserDetailsService {

    private final CustomerRepository customerRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Customer Customer = customerRepository.findByEmail(email);
        if (Customer == null)
            throw new UsernameNotFoundException("Could not find any customer with email: " + email);

        return new ShopmeCustomerUserDetails(Customer);
    }
}
