package com.shopme.admin.user;

import com.shopme.common.entity.Role;
import com.shopme.common.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public List<User> listAll() {
        return (List<User>) userRepository.findAll();
    }

    public User save(User user){
        return userRepository.save(user);
    }

    public List<Role> listRoles(){
        return (List<Role>) roleRepository.findAll();
    }
}
