package com.shopme.customer.general.repository;

import com.shopme.common.entity.Customer;
import com.shopme.common.entity.setting.country.Country;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
public class CustomerRepositoryTests {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    public void CustomerRepository_Save_ReturnSavedCustomer() {
        Country country = entityManager.find(Country.class, 1);

        Customer customer = Customer.builder()
                .email("test.customer@example.com")
                .password("password123")
                .firstName("John")
                .lastName("Doe")
                .phoneNumber("1234567890")
                .addressLine1("123 Main St")
                .city("New York")
                .state("NY")
                .country(country)
                .postalCode("10001")
                .enabled(true)
                .build();

        Customer savedCustomer = customerRepository.save(customer);

        assertThat(savedCustomer).isNotNull();
        assertThat(savedCustomer.getId()).isGreaterThan(0);
    }

    @Test
    public void CustomerRepository_FindAll_ReturnAllCustomers() {
        Iterable<Customer> customers = customerRepository.findAll();
        customers.forEach(System.out::println);

        assertThat(customers).hasSizeGreaterThan(0);
    }

    @Test
    public void CustomerRepository_FindById_ReturnCustomer() {
        Customer customer = customerRepository.findById(1).orElse(null);
        assertThat(customer).isNotNull();
        System.out.println(customer);
    }

    @Test
    public void CustomerRepository_Update_ReturnUpdatedCustomer() {
        Integer customerId = 1;
        Customer customer = customerRepository.findById(customerId).get();

        customer.setPhoneNumber("9876543210");
        customer.setAddressLine1("456 Updated St");
        customer.setEnabled(false);

        Customer updatedCustomer = customerRepository.save(customer);

        assertThat(updatedCustomer.getPhoneNumber()).isEqualTo("9876543210");
        assertThat(updatedCustomer.getAddressLine1()).isEqualTo("456 Updated St");
    }

    @Test
    public void CustomerRepository_Delete_Void() {
        Integer customerId = 5;
        customerRepository.deleteById(customerId);

        Optional<Customer> result = customerRepository.findById(customerId);
        assertThat(result).isNotPresent();
    }

    @Test
    public void CustomerRepository_FindByEmail_ReturnCustomer() {
        String email = "test.customer@example.com";
        Customer customer = customerRepository.findByEmail(email);

        assertThat(customer).isNotNull();
        assertThat(customer.getEmail()).isEqualTo(email);
    }

    @Test
    public void CustomerRepository_FindByVerificationCode_ReturnCustomer() {
        // First, create a customer with a verification code
        Customer customer = customerRepository.findById(1).get();
        customer.setEmail("verify.me@example.com");
        customer.setVerificationCode("test_code_123");
        customerRepository.save(customer);

        // Then test finding by verification code
        Customer foundCustomer = customerRepository.findByVerificationCode("test_code_123");

        assertThat(foundCustomer).isNotNull();
        assertThat(foundCustomer.getVerificationCode()).isEqualTo("test_code_123");
    }

    @Test
    public void CustomerRepository_EnableCustomer_ReturnCustomer() {
        Integer customerId = 1; // Assuming customer with ID 1 exists
        Customer customer = customerRepository.enable(customerId);
//        customerRepository.enable(customerId);
//        Customer customer = customerRepository.findById(customerId).get();

        assertThat(customer.isEnabled()).isTrue();
    }
}