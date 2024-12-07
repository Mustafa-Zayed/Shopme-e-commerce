package com.shopme.admin.user;

import com.shopme.common.entity.Role;
import com.shopme.common.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public List<User> listAll() {
        return (List<User>) userRepository.findAll();
    }

    public List<Role> listRoles() {
        return (List<Role>) roleRepository.findAll();
    }

    public void save(User user) {
        // if editing user case and password field is empty => keep password
        // the same as the one in the database, not changed, not encoded again
        boolean isUpdatingUser = user.getId() != null;
//        if (isUpdatingUser && user.getPassword().isEmpty()) {
//            User userInDb = userRepository.findById(user.getId()).get();
//            user.setPassword(userInDb.getPassword());
//            userRepository.save(user);
//            return;
//        }
//        encodePassword(user);
//        userRepository.save(user);
        if (isUpdatingUser && user.getPassword().isEmpty()) {
            User userInDb = userRepository.findById(user.getId()).get();
            user.setPassword(userInDb.getPassword());
        } else
            encodePassword(user);

        userRepository.save(user);
    }

    private void encodePassword(User user) {
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
    }

    public boolean isEmailUnique(String email, Integer id) {
        /*
        String emailInDatabase = "";
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) // update user
            emailInDatabase = user.get().getEmail();

        return userRepository.findByEmail(email) == null || email.equals(emailInDatabase); */
        /*
        // check if email is not existed in the database
        if (userRepository.findByEmail(email) == null)
            return true;

        // check if email is the same as the one in the form, user doesn't want to change the email
        String emailInDatabase = "";
        Optional<User> editedUser = userRepository.findById(id);
        if (editedUser.isPresent()) // existed user, edit form, not new user
            // get the email of edited user to check if it's the same as the one in the form.
            emailInDatabase = editedUser.get().getEmail();

        return email.equals(emailInDatabase); */

        // Best Approach
        User byEmail = userRepository.findByEmail(email);
        if (byEmail == null)
            return true;
        // At this point, email is existed in the database, check if it's create new user or edit form

        // unnecessary step
//        if (id == null) // new user and byEmail is existed in the database => not unique
//            return false;
        // At this point, edited user case, check if the email belongs to the edited user
        // (i.e. edited user needn't change the email) or another user in the database

        return byEmail.getId().equals(id);
    }

    public User findById(Integer id) throws UserNotFoundException {
        return userRepository.findById(id).orElseThrow(
                () -> new UserNotFoundException("Could not find any user with Id: " + id)
        );
    }
}
