package com.policyhub.controller;

import com.policyhub.entity.Premium;
import com.policyhub.entity.Premium.PremiumStatus;
import com.policyhub.entity.User;
import com.policyhub.entity.User.Role;
import com.policyhub.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "http://localhost:5500", allowCredentials = "true")
public class DashboardController {

    @Autowired private CustomerRepository customerRepo;
    @Autowired private PolicyRepository policyRepo;
    @Autowired private ClaimRepository claimRepo;
    @Autowired private PremiumRepository premiumRepo;

    @GetMapping("/stats")
    public ResponseEntity<?> getStats(HttpSession session) {
        User u = (User) session.getAttribute("loggedInUser");
        if (u == null || u.getRole() != Role.ADMIN)
            return ResponseEntity.status(403).body("Admin only");

        Map<String, Object> stats = new HashMap<>();
        stats.put("customers", customerRepo.count());
        stats.put("policies", policyRepo.count());
        stats.put("claims", claimRepo.count());
        stats.put("revenue", premiumRepo.findAll().stream()
                .filter(p -> p.getStatus() == PremiumStatus.PAID)
                .map(Premium::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add));

        return ResponseEntity.ok(stats);
    }
}
