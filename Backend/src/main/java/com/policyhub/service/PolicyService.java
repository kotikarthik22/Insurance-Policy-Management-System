package com.policyhub.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.policyhub.entity.Policy;
import com.policyhub.repository.PolicyRepository;

import jakarta.transaction.Transactional;

@Service
public class PolicyService {
	@Autowired
	private PolicyRepository policyRepository;
	@Autowired
	private PremiumService premiumService;
	
	private static Logger log = LoggerFactory.getLogger(PolicyService.class);

	public Policy createPolicy(Policy policy) {

		log.info("Creating Policy with ID: {}",policy.getPolicyId());
		
		policy.setValidityStartDate(LocalDate.now());
		policy.setValidityEndDate(LocalDate.now().plusYears(policy.getDurationYears()));
		BigDecimal amt = premiumService.calculatePremium(policy);
		policy.setPremiumAmount(amt);
		Policy saved= policyRepository.save(policy);
		
		log.info("{} policy is created",policy.getPolicyType());
		return saved;
	}

	public List<Policy> getAllPolicies() {
		return policyRepository.findAll();
	}

	public Policy getPolicy(Integer id) {
		return policyRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Policy not found"));
	}

	public Policy updatePolicy(Integer id, Policy updated) {
		Policy existing = getPolicy(id);
		if (updated.getPolicyType() != null)
			existing.setPolicyType(updated.getPolicyType());
		if (updated.getCoverageAmount() != null)
			existing.setCoverageAmount(updated.getCoverageAmount());
		if (updated.getDurationYears() != null)
			existing.setDurationYears(updated.getDurationYears());

		existing.setValidityStartDate(LocalDate.now());
		existing.setValidityEndDate(LocalDate.now().plusYears(existing.getDurationYears()));
		existing.setPremiumAmount(premiumService.calculatePremium(existing));
		Policy saved= policyRepository.save(existing);
		log.info("Policy {} updated successfully",id);
		return saved;
		
	}

	@Transactional
	public void deletePolicy(Integer id) {
		Policy policy=policyRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Policy not found"));
		
		policy.getCustomers().forEach(c->c.getPolicies().remove(policy));
		policy.getCustomers().clear();
		
		policyRepository.deleteById(id);
		
	}
}
