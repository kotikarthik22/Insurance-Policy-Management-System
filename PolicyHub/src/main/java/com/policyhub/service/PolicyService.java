package com.policyhub.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.policyhub.entity.Policy;
import com.policyhub.repository.PolicyRepository;

@Service
public class PolicyService {
    @Autowired private PolicyRepository policyRepository;
    @Autowired private PremiumService premiumService;

    public Policy createPolicy(Policy policy) {
        policy.setValidityStartDate(LocalDate.now());
        policy.setValidityEndDate(LocalDate.now().plusYears(policy.getDurationYears()));
        // calculate via premiumService
        BigDecimal amt = premiumService.calculatePremium(policy);
        policy.setPremiumAmount(amt);
        return policyRepository.save(policy);
    }

    public List<Policy> getAllPolicies() { return policyRepository.findAll(); }

    public Policy getPolicy(Integer id) {
        return policyRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Policy not found"));
    }

    public Policy updatePolicy(Integer id, Policy updated) {
        Policy existing = getPolicy(id);
        if (updated.getPolicyType() != null) existing.setPolicyType(updated.getPolicyType());
        if (updated.getCoverageAmount() != null) existing.setCoverageAmount(updated.getCoverageAmount());
        if (updated.getDurationYears() != null) existing.setDurationYears(updated.getDurationYears());

        existing.setValidityStartDate(LocalDate.now());
        existing.setValidityEndDate(LocalDate.now().plusYears(existing.getDurationYears()));
        existing.setPremiumAmount(premiumService.calculatePremium(existing));
        return policyRepository.save(existing);
    }

    public void deletePolicy(Integer id) { policyRepository.deleteById(id); }
}
