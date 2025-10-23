package com.policyhub.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.policyhub.entity.Policy;
import com.policyhub.entity.User;
import com.policyhub.entity.User.Role;
import com.policyhub.service.PolicyService;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/policies")
@CrossOrigin(origins = "http://localhost:5500", allowCredentials = "true")
public class PolicyController {
    @Autowired private PolicyService policyService;

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody Policy policy, HttpSession session) {
        User u = (User) session.getAttribute("loggedInUser");
        if (u == null || u.getRole() != Role.ADMIN) return ResponseEntity.status(403).body("Admin only");
        try {
            Policy created = policyService.createPolicy(policy);
            return ResponseEntity.ok(created);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<Policy>> all() { return ResponseEntity.ok(policyService.getAllPolicies()); }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(policyService.getPolicy(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Not found");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id, @RequestBody Policy p, HttpSession session) {
        User u = (User) session.getAttribute("loggedInUser");
        if (u == null || u.getRole() != Role.ADMIN) return ResponseEntity.status(403).body("Admin only");
        try {
            return ResponseEntity.ok(policyService.updatePolicy(id, p));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id, HttpSession session) {
        User u = (User) session.getAttribute("loggedInUser");
        if (u == null || u.getRole() != Role.ADMIN) return ResponseEntity.status(403).body("Admin only");
        try {
            policyService.deletePolicy(id);
            return ResponseEntity.ok("Deleted");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Delete failed");
        }
    }
}
