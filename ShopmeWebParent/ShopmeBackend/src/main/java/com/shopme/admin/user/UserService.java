package com.shopme.admin.user;

import com.shopme.admin.utils.FileUploadUtil;
import com.shopme.common.entity.Role;
import com.shopme.common.entity.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@Service
public class UserService {
    public static final int USERS_PER_PAGE = 4;

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public List<User> listAll() {
        return (List<User>) userRepository.findAll(Sort.by("firstName").ascending());
    }

    public Page<User> listByPageWithSorting(int pageNumber, String sortField, String sortDir,
                                            String keyword) {
        Sort sort = Sort.by(sortField);
        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();

        // page number is a 0-based index, but sent from the client as a 1-based index, so we need to subtract 1.
        Pageable pageable = PageRequest.of(pageNumber - 1, USERS_PER_PAGE, sort);

        if (keyword.isEmpty())
            return userRepository.findAll(pageable);

        return userRepository.findAll(keyword, pageable);
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

    public void saveUserAndImage(User user,
                                 MultipartFile multipart,
                                 RedirectAttributes redirectAttributes) throws IOException {
//        // Server-Side Rendering Approach
//        if (!userService.isEmailUnique(user.getEmail())) {
//            model.addAttribute("emailError", "Email is already in use!");
//            model.addAttribute("listRoles", userService.listRoles());
//            return "user_form";
//        }

        // If we need to check for the id, we must do that before saving the user, as user object
        // will be updated with the new id after saving.
        String message;
        message = user.getId() == null ?
                "New User has been created!" : "User has been updated successfully!";

        if (!multipart.isEmpty()) {
            String originalFilename = StringUtils.cleanPath(Objects.requireNonNull(multipart.getOriginalFilename()));
            user.setPhotos(originalFilename);
            User savedUser = save(user); // user == savedUser: true
            System.out.println("user.getId(): " + user.getId());
            String uploadDir = "user-photos/" + savedUser.getId(); // user.getId() works as well, because the user and savedUser objects are the same.
            FileUploadUtil.saveFile(uploadDir, originalFilename, multipart);
        } else {
            System.out.println("user.getPhotos(): " + user.getPhotos()); // user.getPhotos():
            // In the create mode, if the user does not upload a new file, photos field will sent as
            // empty string "", not null, because of <input type="hidden" th:field="*{photos}"> in the
            // user_form, and that will cause a constraint violation in getPhotosImagePath() method in
            // User class. So we must set photos to null.
            if (user.getPhotos().isEmpty())
                user.setPhotos(null);
            save(user);
        }
//        // Incorrect approach
//        // user.getId() => will never be null, because the user gets a new ID when saved, so we need to check before saving.
//        if (user.getId() == null) // new user
//            redirectAttributes.addFlashAttribute("message", "User has been Created!");
//        else // edit user
//            redirectAttributes.addFlashAttribute("message", "User has been saved successfully!");

        redirectAttributes.addFlashAttribute("message", message);
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
