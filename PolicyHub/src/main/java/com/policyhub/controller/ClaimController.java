package com.policyhub.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.policyhub.entity.Claim;
import com.policyhub.entity.Claim.ClaimStatus;
import com.policyhub.entity.User;
import com.policyhub.entity.User.Role;
import com.policyhub.service.ClaimService;
import com.policyhub.service.CustomerService;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/claims")
@CrossOrigin(origins = "http://localhost:5500", allowCredentials = "true")
public class ClaimController {
    @Autowired private ClaimService claimService;

    @PostMapping("/customer/{customerId}/policy/{policyId}")
    public ResponseEntity<?> create(@PathVariable Integer customerId, @PathVariable Integer policyId,
                                    @RequestBody Claim claim, HttpSession session) {
        User u = (User) session.getAttribute("loggedInUser");
        if (u == null) return ResponseEntity.status(401).body("Login required");
        try {
            return ResponseEntity.ok(claimService.createClaim(customerId, policyId, claim));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<?> byCustomer(@PathVariable Integer customerId, HttpSession session) {
        User u = (User) session.getAttribute("loggedInUser");
        if (u == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(claimService.getByCustomer(customerId));
    }

    @PutMapping("/{claimId}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Integer claimId, @RequestParam ClaimStatus status, HttpSession session) {
        User u = (User) session.getAttribute("loggedInUser");
        if (u == null || u.getRole() != Role.ADMIN){
             return ResponseEntity.status(403).body("Admin only");
        }
        try {
            return ResponseEntity.ok(claimService.updateStatus(claimId, status));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getClaim(@PathVariable Integer id, HttpSession session) {
        User u = (User) session.getAttribute("loggedInUser");
        if (u == null) return ResponseEntity.status(401).build();
        try {
            return ResponseEntity.ok(claimService.getClaim(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Claim not found");
        }
    }

    @GetMapping
    public ResponseEntity<List<Claim>> all(HttpSession session) {
        User u = (User) session.getAttribute("loggedInUser");
        if (u == null || u.getRole() != Role.ADMIN) return ResponseEntity.status(403).build();
        return ResponseEntity.ok(claimService.getAll());
    }
}
