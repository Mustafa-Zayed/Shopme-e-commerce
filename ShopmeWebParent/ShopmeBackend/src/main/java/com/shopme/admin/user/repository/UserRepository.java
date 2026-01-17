package com.shopme.admin.user.repository;

import com.shopme.admin.utility.paging_and_sorting.SearchRepository;
import com.shopme.common.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends SearchRepository<User, Integer> {
    User findByEmail(String email);

    Long countById(Integer id);

    // Instead of updating the whole user, we can just update the user's status
    // @Query("UPDATE User u SET u.enabled = :enabled WHERE u.id = :id")
    @Query("UPDATE User u SET u.enabled = ?2 WHERE u.id = ?1")
    @Modifying
    void updateEnabledStatus(Integer id, boolean enabled);

    @Query("SELECT u FROM User u WHERE CONCAT(u.id, ' ', u.email, ' ', u.firstName, ' ', u.lastName) " +
            "LIKE %?1%")
    Page<User> findAll(String keyword, Pageable pageable);
}
