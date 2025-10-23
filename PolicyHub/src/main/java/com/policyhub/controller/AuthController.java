package com.policyhub.controller;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.policyhub.entity.Customer;
import com.policyhub.entity.User;
import com.policyhub.entity.User.Role;
import com.policyhub.repository.UserRepository;
import com.policyhub.service.CustomerService;
import com.policyhub.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5500", allowCredentials = "true")
public class AuthController {
    @Autowired
    private UserService userService;
    @Autowired 
    private UserRepository userRepository;
    @Autowired
     private CustomerService customerService;

    // @GetMapping("/init-admin")
    // public ResponseEntity<String> createAdminIfNotExists() {
    //     Optional<User> adminCheck = userRepository.findByUsername("admin");
    //     if (adminCheck.isEmpty()) {
    //         User admin = new User();
    //         admin.setUsername("admin");
    //         admin.setPassword("admin");
    //         admin.setRole(Role.ADMIN);
    //         userRepository.save(admin);
    //         return ResponseEntity.ok("Default admin created");
    //     }
    //     return ResponseEntity.ok("Admin exists");
    // }

    // Register expects a Customer JSON with nested user: { user: { username, password }, fullName, email, phone, address }
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody Customer customer) {
        try {
          
            User u = customer.getUser();
            if (u == null) throw new IllegalArgumentException("User credentials required");
            u.setRole(Role.CUSTOMER);
            User savedUser = userService.register(u);
            
            customer.setUser(savedUser);
            Customer saved = customerService.createCustomer(customer);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Registration failed: " + e.getMessage());
        }
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String username, @RequestParam String password, HttpSession session) {
        try {
            User u = userService.login(username, password);
            session.setAttribute("loggedInUser", u);
            return ResponseEntity.ok(u);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(401).body(ex.getMessage());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok("Logged out");
    }

    @GetMapping("/current")
    public ResponseEntity<?> current(HttpSession session) {
        User u = (User) session.getAttribute("loggedInUser");
        if (u == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(userService.getById(u.getUserId()));
    }
}

