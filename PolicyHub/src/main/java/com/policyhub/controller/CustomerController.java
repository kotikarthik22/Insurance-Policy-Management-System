package com.policyhub.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.policyhub.entity.Customer;
import com.policyhub.entity.User;
import com.policyhub.entity.User.Role;
import com.policyhub.service.ClaimService;
import com.policyhub.service.CustomerService;
import com.policyhub.service.PremiumService;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/customers")
@CrossOrigin(origins = "http://localhost:5500", allowCredentials = "true")
public class CustomerController {
    @Autowired private CustomerService customerService;
    @Autowired private ClaimService claimService;
    @Autowired private PremiumService premiumService;

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody Customer customer) {
        try {
            return ResponseEntity.ok(customerService.createCustomer(customer));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<?> profile(HttpSession session) {
        User u = (User) session.getAttribute("loggedInUser");
        if (u == null) return ResponseEntity.status(401).build();
        try {
            return ResponseEntity.ok(customerService.getByUserId(u.getUserId()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{customerId}/buy/{policyId}")
    public ResponseEntity<?> buy(@PathVariable Integer customerId, @PathVariable Integer policyId, HttpSession session) {
        User u = (User) session.getAttribute("loggedInUser");
        if (u == null) return ResponseEntity.status(401).body("Login required");
        try {
            Customer c = customerService.getByUserId(u.getUserId());
            if (!c.getCustomerId().equals(customerId)) return ResponseEntity.status(403).body("Not authorized");
            boolean already = c.getPolicies().stream().anyMatch(p -> p.getPolicyId().equals(policyId));
            if (already) return ResponseEntity.badRequest().body("Policy already purchased");
            customerService.buyPolicy(customerId, policyId);
            return ResponseEntity.ok("Policy purchased and premium generated");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(@RequestBody Customer updated, HttpSession session) {
        User u = (User) session.getAttribute("loggedInUser");
        if (u == null) return ResponseEntity.status(401).build();
        try {
            return ResponseEntity.ok(customerService.updateProfile(u.getUserId(), updated));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/policies")
    public ResponseEntity<?> getMyPolicies(HttpSession session) {
        User u = (User) session.getAttribute("loggedInUser");
        if (u == null) return ResponseEntity.status(401).build();
        try {
            Customer c = customerService.getByUserId(u.getUserId());
            return ResponseEntity.ok(c.getPolicies());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/claims")
    public ResponseEntity<?> getMyClaims(HttpSession session) {
        User u = (User) session.getAttribute("loggedInUser");
        if (u == null) return ResponseEntity.status(401).build();
        try {
            Customer c = customerService.getByUserId(u.getUserId());
            return ResponseEntity.ok(claimService.getByCustomer(c.getCustomerId()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/premiums")
    public ResponseEntity<?> getMyPremiums(HttpSession session) {
        User u = (User) session.getAttribute("loggedInUser");
        if (u == null) return ResponseEntity.status(401).build();
        try {
            Customer c = customerService.getByUserId(u.getUserId());
            return ResponseEntity.ok(premiumService.getByCustomer(c.getCustomerId()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> all(HttpSession session) {
        User u = (User) session.getAttribute("loggedInUser");
        if (u == null || u.getRole() != Role.ADMIN) return ResponseEntity.status(403).build();
        return ResponseEntity.ok(customerService.getAllCustomers());
    }
}
