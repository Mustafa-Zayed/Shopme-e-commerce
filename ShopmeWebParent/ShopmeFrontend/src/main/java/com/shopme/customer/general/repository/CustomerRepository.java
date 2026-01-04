package com.shopme.customer.general.repository;

import com.shopme.common.entity.Customer;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface CustomerRepository extends CrudRepository<Customer, Integer>, PagingAndSortingRepository<Customer, Integer> {
    Customer findByEmail(String email);

    @Query("SELECT c FROM Customer c WHERE CONCAT(c.firstName, ' ', c.lastName) = :fullName")
    Customer findByFullName(String fullName);

    Customer findByPhoneNumber(String phoneNumber);

    Customer findByVerificationCode(String code);

//    default Customer enable(Integer customerId) {
//        Customer customer = findById(customerId).get();
//        customer.setEnabled(true);
//        customer.setVerificationCode(null);
//        return save(customer);
//    }

    @Modifying
    @Query("UPDATE Customer c SET c.enabled = TRUE, c.verificationCode = null WHERE c.id = :customerId")
    void enable(Integer customerId);
}