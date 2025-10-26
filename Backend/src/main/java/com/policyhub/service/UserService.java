package com.policyhub.service;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.policyhub.entity.User;
import com.policyhub.entity.User.Role;
import com.policyhub.repository.UserRepository;

@Service
public class UserService {
	@Autowired
	private PasswordEncoder passEncoder;

	@Autowired
	private UserRepository userRepository;

	private static Logger log = LoggerFactory.getLogger(UserService.class);

	public User register(User user) {
		Optional<User> existing = userRepository.findByUsername(user.getUsername());
		if (existing.isPresent()) {
			log.warn("Username already exists");
			throw new IllegalArgumentException("Username already exists");
		}
		if (user.getRole() == null)
			user.setRole(Role.CUSTOMER);
		user.setPassword(passEncoder.encode(user.getPassword()));
		User savedUser= userRepository.save(user);
		log.info("User {} registered successfully",user.getUsername());
		return savedUser;
	}

	public User login(String username, String rawPassword) {
        User user= userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));
        
        if(!passEncoder.matches(rawPassword, user.getPassword())) {
        	log.warn("Login failed: Incorrect password for username {}",username);
        	throw new IllegalArgumentException("Invalid credentials");
        }
        return user;
    }

	public User getById(Integer id) {
		return userRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("User not found"));
	}
}
