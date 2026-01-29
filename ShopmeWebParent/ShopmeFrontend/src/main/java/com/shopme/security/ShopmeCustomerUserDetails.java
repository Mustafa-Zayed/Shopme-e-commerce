package com.shopme.security;

import com.shopme.common.entity.Customer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public record ShopmeCustomerUserDetails(Customer customer) implements UserDetails {

    @Override
    public String getPassword() {
        return customer.getPassword();
    }

    @Override
    public String getUsername() {
        return customer.getEmail();
    }

    @Override
    public boolean isEnabled() {
        return customer.isEnabled();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    public String getFullName() {
        return customer.getFullName();
    }

    public void setFirstName(String firstName) {
        this.customer.setFirstName(firstName);
    }

    public void setLastName(String lastName) {
        this.customer.setLastName(lastName);
    }
}
