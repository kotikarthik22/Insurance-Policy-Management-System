package com.policyhub.service;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.policyhub.entity.User;
import com.policyhub.entity.User.Role;
import com.policyhub.repository.UserRepository;

@Service
public class UserService {
    @Autowired private UserRepository userRepository;

    public User register(User user) {
        Optional<User> existing = userRepository.findByUsername(user.getUsername());
        if (existing.isPresent()) throw new IllegalArgumentException("Username already exists");
        if (user.getRole() == null) user.setRole(Role.CUSTOMER);
        return userRepository.save(user);
    }

    public User login(String username, String password) {
        return userRepository.findByUsernameAndPassword(username, password)
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));
    }

    public User getById(Integer id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }
}
