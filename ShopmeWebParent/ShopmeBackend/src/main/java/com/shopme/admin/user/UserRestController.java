package com.shopme.admin.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class UserRestController {
    private final UserService userService;

    // Consumed by Ajax call in user_form.html
    @PostMapping("/users/check_email")
    public String checkDuplicateEmail(@RequestParam String email,
                                      @RequestParam(defaultValue = "") Integer id
//                                      @RequestParam(defaultValue = "0") Integer id
    ) {
        // defaultValue = "0" means the default value of id is 0 if it is not specified in the request or
        // sent as "&id=&" (i.e. In case of creating a new user, id sent as null). And, defaultValue = ""
        // converts the id to null value if it is not specified in the request or sent as "&id=&".

        return userService.isEmailUnique(email, id) ? "OK" : "Duplicated";
    }

//    @DeleteMapping("/users/delete/{id}")
//    public String deleteUser(@PathVariable int id) {
//        userService.delete(id);
//
//        try {
//            userService.findById(id);
//            return "OK";
//        } catch (UserNotFoundException e) {
//            return "Exception";
//        }
//    }
}
