package com.shopme.admin.user;

import com.shopme.common.entity.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
public class RoleRepositoryTests {

    @Autowired
    private RoleRepository roleRepository;

    private Role roleAdmin, roleSalesperson, roleEditor, roleShipper, roleAssistant;

    @BeforeEach
    public void setup() {
        //Arrange
        roleAdmin = Role.builder().name("Admin").description("Manage everything").build();
        roleSalesperson = Role.builder().name("Salesperson").description("Manage product price, customers, shipping, orders, and sales reports").build();
        roleEditor = Role.builder().name("Editor").description("Manage categories, brands, products, articles, and menus").build();
        roleShipper = Role.builder().name("Shipper").description("View products, view orders, and update order status").build();
        roleAssistant = Role.builder().name("Assistant").description("Manage questions and reviews").build();
    }

    @Test
    public void RoleRepository_Save_ReturnSavedRole() {
        //Act
        Role saved = roleRepository.save(roleAdmin);

        //Assert
        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isGreaterThan(0);
    }

    @Test
    public void RoleRepository_SaveAll_ReturnSavedRoles() {
        //Act
        List<Role> savedAll = (List<Role>) roleRepository.saveAll(List.of(roleAdmin, roleSalesperson, roleEditor, roleShipper, roleAssistant));

        //Assert
        assertThat(savedAll.size()).isEqualTo(5);
        assertThat(savedAll).isNotNull();
    }
}
