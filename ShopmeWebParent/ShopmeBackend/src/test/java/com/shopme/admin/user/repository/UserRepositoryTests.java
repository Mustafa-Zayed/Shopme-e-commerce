package com.shopme.admin.user.repository;

import com.shopme.common.entity.Role;
import com.shopme.common.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
public class UserRepositoryTests {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager testEntityManager; // We need this to get the roles, instead of RoleRepository

    private User userMustafa;
    private User userMoaz;

    @BeforeEach
    public void setup() {
        //Arrange
        userMustafa = User.builder()
                .email("mustafa@gmail.com")
                .password("mustafa2002")
                .firstName("Mustafa")
                .lastName("Zayed")
                .build();

        Role roleAdmin = testEntityManager.find(Role.class, 1);
        userMustafa.addRole(roleAdmin);

        userMoaz = User.builder()
                .email("moaz@gmail.com")
                .password("moaz2002")
                .firstName("Moaz")
                .lastName("Ehab")
                .build();

//        Role roleEditor = testEntityManager.find(Role.class, 3);
        // If the Role entity already exists in the database, JPA will not attempt to persist it again
        // as long as the Role object has the correct id and is properly used in the context of the relationship
        // Not Recommended approach, use 'Fetching from the Database' approach instead
        Role roleEditor = Role.builder().id(3).build();
        Role roleAssistant = Role.builder().id(5).build();

        userMoaz.addRole(roleEditor);
        userMoaz.addRole(roleAssistant);
    }

    @Test
    public void UserRepository_Save_ReturnSavedUserWithOneRole() {
        //Act
        User saved = userRepository.save(userMustafa);

        //Assert
        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isGreaterThan(0);
    }

    @Test
    public void UserRepository_Save_ReturnSavedUserWithTwoRoles() {
        //Act
        User saved = userRepository.save(userMoaz);

        //Assert
        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isGreaterThan(0);
        assertThat(saved.getRoles().size()).isEqualTo(2);
    }

    @Test
    public void UserRepository_FindAll_ReturnUsers() {
        //Act
        List<User> users = (List<User>) userRepository.findAll();

        //Assert
        assertThat(users).isNotNull();
        assertThat(users.size()).isEqualTo(2);
    }

    @Test
    public void UserRepository_FindById_ReturnUser() {
        //Act
        User user = userRepository.findById(1).get();

        //Assert
        assertThat(user).isNotNull();
        assertThat(user.getId()).isEqualTo(1);
    }

    @Test
    public void UserRepository_UpdateUserEnabled_ReturnUpdatedUser() {
        User user = userRepository.findById(1).get();

        //Act
        user.setEnabled(true);

        //Assert
        assertThat(user).isNotNull();
        assertThat(user.isEnabled()).isEqualTo(true);
    }

    @Test
    public void UserRepository_UpdateUserRoles_ReturnUpdatedUser() {
        User user = userRepository.findById(2).get();
        Role roleAssistant = Role.builder().id(5).build();// requires implementing equals and hashCode methods as this 's a new object different from the one in the database and removing or finding operations in sets depends on equals and hashCode
//        Role roleAssistant = testEntityManager.find(Role.class, 5);// recommended approach
        Role roleEditor = Role.builder().id(2).build();

        //Act
        user.getRoles().remove(roleAssistant);
        user.addRole(roleEditor);

        //Assert
        System.out.println(user.getRoles());
        assertThat(user.getRoles().size()).isEqualTo(2);
    }

    @Test
    public void UserRepository_DeleteUserById_ReturnUserIsEmpty() {
        //Act
        userRepository.deleteById(2);

        Optional<User> optionalUser = userRepository.findById(2);

        //Assert
        assertThat(optionalUser).isEmpty();
    }

    @Test
    public void UserRepository_FindByEmail_ReturnUser() {
        //Act
        User user1 = userRepository.findByEmail("mustafa@gmail.com");
        User user2 = userRepository.findByEmail("a@a.com");

        System.out.println(user1);
        System.out.println(user2);

        //Assert
        assertThat(user1).isNotNull();
        assertThat(user2).isNull();
    }

    @Test
    public void UserRepository_CountById_ReturnCount() {

        Long count1 = userRepository.countById(1);
        Long count2 = userRepository.countById(9999);

        System.out.println(count1);
        System.out.println(count2);

        assertThat(count1).isNotNull().isGreaterThan(0);
        assertThat(count2).isNotNull().isEqualTo(0);
    }

    @Test
    public void UserRepository_EnableUserStatus_Void() {
        userRepository.updateEnabledStatus(1, true);
    }

    @Test
    public void UserRepository_DisableUserStatus_Void() {
        userRepository.updateEnabledStatus(1, false);
    }

    @Test
    public void UserRepository_PaginateUsers_ReturnUsers() {
        int pageNo = 1, pageSize = 4;
        Pageable pageRequest = PageRequest.of(pageNo, pageSize);
        Page<User> userPage = userRepository.findAll(pageRequest);

        List<User> userList = userPage.getContent();
        userList.forEach(System.out::println);

        assertThat(userList).isNotNull();
        assertThat(userList.size()).isEqualTo(4);
    }

    @Test
    public void UserRepository_PaginateUsersWithSorting_ReturnUsers() {
        int pageNo = 1, pageSize = 4;
        String sortField = "firstName", sortDir = "asc";

        Sort sort = Sort.by(sortField);
        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();

        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
        Page<User> userPage = userRepository.findAll(pageable);

        List<User> userList = userPage.getContent();
        userList.forEach(System.out::println);

        assertThat(userList).isNotNull();
        assertThat(userList.size()).isEqualTo(4);
    }

    @Test
    public void UserRepository_PaginateUsersWithSortingAndFilter_ReturnUsers() {
        int pageNo = 1, pageSize = 23;
        String sortField = "id", sortDir = "asc", keyword = "gmail";

        Sort sort = Sort.by(sortField);
        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();

        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
        Page<User> userPage = userRepository.findAll(keyword, pageable);

        List<User> userList = userPage.getContent();
        userList.forEach(System.out::println);

        assertThat(userList).isNotNull();
        assertThat(userList.size()).isEqualTo(15);
        assertThat(userList.size()).isGreaterThan(0);
    }
}
