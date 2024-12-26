package com.shopme.admin.user;

import com.shopme.common.entity.Role;
import com.shopme.common.entity.User;
import jakarta.transaction.Transactional;
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

    public User save(User user) {
        // if editing user case and password field is empty => keep password
        // the same as the one in the database, not changed, not encoded again
        boolean isUpdatingUser = user.getId() != null;

        if (isUpdatingUser && user.getPassword().isEmpty()) {
            User userInDb = userRepository.findById(user.getId()).get();
            user.setPassword(userInDb.getPassword());
        } else
            encodePassword(user);

        return userRepository.save(user);
    }

    private void encodePassword(User user) {
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
    }

    public boolean isEmailUnique(String email, Integer id) {
        User byEmail = userRepository.findByEmail(email);
        if (byEmail == null)
            return true;

        // Check if the email belongs to the edited user (i.e. edited user needn't change the email)
        // If new user case, returns false as the id param is null
        return byEmail.getId().equals(id);
    }

    public User findById(Integer id) throws UserNotFoundException {
        return userRepository.findById(id).orElseThrow(
                () -> new UserNotFoundException("Could not find any user with Id: " + id)
        );
    }

    public void delete(int id) throws UserNotFoundException {
        // Instead of using findById() method that will return a full User object with the all details,
        // we can use countById() method which will only return 0 or 1 to check if the user exists
        Long count = userRepository.countById(id);
        if (count == 0)
            throw new UserNotFoundException("Could not find any user with Id: " + id);

        userRepository.deleteById(id);
    }

    @Transactional
    public void updateUserEnabledStatus(int id, boolean status) throws UserNotFoundException {
        // This approach is not recommended as it will update a full User object
        /*User user = findById(id);
        user.setEnabled(!user.isEnabled());
        userRepository.save(user);*/

        // Instead of updating the whole user, we can just update the user's status
        userRepository.updateEnabledStatus(id, status);
    }
}
