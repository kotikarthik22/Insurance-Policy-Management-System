package com.policyhub.controller;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import com.policyhub.entity.Customer;
import com.policyhub.entity.User;
import com.policyhub.entity.User.Role;
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
	private CustomerService customerService;
	private static Logger log = LoggerFactory.getLogger(AuthController.class);

	@PostMapping("/register")
	public ResponseEntity<?> register(@Valid @RequestBody Customer customer, BindingResult result) {

		if (result.hasErrors()) {
			Map<String, String> errors = new HashMap<>();

			result.getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
			return ResponseEntity.badRequest().body(errors);
		}

		try {

			User u = customer.getUser();
			if (u == null)
				throw new IllegalArgumentException("User credentials required");
			u.setRole(Role.CUSTOMER);
			User savedUser = userService.register(u);

			customer.setUser(savedUser);
			Customer saved = customerService.createCustomer(customer);
			return ResponseEntity.ok(saved);
		}catch (IllegalArgumentException ex) {
			return ResponseEntity.badRequest().body(Map.of("Error", ex.getMessage()));
		}
		catch (Exception e) {
			return ResponseEntity.badRequest().body("Registration failed: " + e.getMessage());
		}
	}

	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestParam String username, @RequestParam String password, HttpSession session) {
		try {
			User u = userService.login(username, password);
			session.setAttribute("loggedInUser", u);
			log.info("Session Created");
			return ResponseEntity.ok(u);
		} catch (IllegalArgumentException ex) {
			return ResponseEntity.status(401).body(ex.getMessage());
		}
	}

	@PostMapping("/logout")
	public ResponseEntity<String> logout(HttpSession session) {
		session.invalidate();
		log.info("Session closed");
		return ResponseEntity.ok("Logged out");
	}

	@GetMapping("/current")
	public ResponseEntity<?> current(HttpSession session) {
		User u = (User) session.getAttribute("loggedInUser");
		if (u == null)
			return ResponseEntity.status(401).build();
		return ResponseEntity.ok(userService.getById(u.getUserId()));
	}
}
