package com.shopme.admin.customer.general.repository;

import com.shopme.admin.utility.paging_and_sorting.SearchRepository;
import com.shopme.common.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface CustomerRepository extends SearchRepository<Customer, Integer> {
    Integer countById(Integer id);

    Customer findByEmail(String email);

    @Query("SELECT c FROM Customer c WHERE CONCAT(c.firstName, ' ', c.lastName) = :fullName")
    Customer findByFullName(String fullName);

    Customer findByPhoneNumber(String phoneNumber);

    /**
     * <h3>Differences between LIKE and CONCAT approaches</h3>
     *
     * <h4>Null handling</h4>
     * <ul>
     *   <li><b>OR version:</b> If one field is NULL, no problem — other fields can still match.</li>
     *   <li><b>CONCAT version:</b> If any of the concatenated fields is NULL, the whole CONCAT may become NULL
     *       (depending on DB), and then the LIKE won’t match. <br>
     *       → This means CONCAT version might miss results unless you use <code>COALESCE(field, '')</code>.
     *   </li>
     * </ul>
     *
     * <h4>Performance</h4>
     * <ul>
     *   <li><b>OR version:</b> Database can optimize per-column conditions, sometimes with indexes.</li>
     *   <li><b>CONCAT version:</b> Harder to optimize, since it builds a big string. Usually slower.</li>
     * </ul>
     *
     * <h4>Flexibility</h4>
     * <ul>
     *   <li><b>OR version:</b> Lets you choose which field matched (useful for debugging).</li>
     *   <li><b>CONCAT version:</b> Hides where it matched.</li>
     * </ul>
     */
    @Query("SELECT p FROM Customer p WHERE " +
            "p.firstName LIKE %?1% " +
            "OR p.lastName LIKE %?1% " +
            "OR CONCAT(p.firstName, ' ', p.lastName) LIKE %?1% " +
            "OR p.email LIKE %?1% " +
            "OR p.addressLine1 LIKE %?1% " +
            "OR p.addressLine2 LIKE %?1% " +
            "OR p.city LIKE %?1% " +
            "OR p.state LIKE %?1% " +
            "OR p.country.name LIKE %?1% " +
            "OR p.postalCode LIKE %?1% ")
    Page<Customer> findAll(String keyword, Pageable pageable);

    @Modifying
    @Query("UPDATE Customer p SET p.enabled = ?2 WHERE p.id = ?1")
    void updateCustomerEnabledStatus(int id, boolean status);
}
