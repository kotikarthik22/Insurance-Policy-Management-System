package com.policyhub.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.policyhub.entity.User;
import com.policyhub.entity.User.Role;
import com.policyhub.service.PremiumService;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/premiums")
@CrossOrigin(origins = "http://localhost:5500", allowCredentials = "true")
public class PremiumController {
    @Autowired private PremiumService premiumService;

    @PostMapping("/generate")
    public ResponseEntity<?> generate(@RequestParam Integer customerId, @RequestParam Integer policyId, HttpSession session) {
        User u = (User) session.getAttribute("loggedInUser");
        if (u == null) return ResponseEntity.status(401).build();
        try {
            return ResponseEntity.ok(premiumService.generatePremium(customerId, policyId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<?> byCustomer(@PathVariable Integer customerId, HttpSession session) {
        User u = (User) session.getAttribute("loggedInUser");
        if (u == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(premiumService.getByCustomer(customerId));
    }

    @PutMapping("/{id}/pay")
    public ResponseEntity<?> pay(@PathVariable Integer id, HttpSession session) {
        User u = (User) session.getAttribute("loggedInUser");
        if (u == null) return ResponseEntity.status(401).build();
        try {
            return ResponseEntity.ok(premiumService.markPaid(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> all(HttpSession session) {
        User u = (User) session.getAttribute("loggedInUser");
        if (u == null || u.getRole() != Role.ADMIN) return ResponseEntity.status(403).body("Admin only");
        return ResponseEntity.ok(premiumService.getAllPremiums());
    }
}
