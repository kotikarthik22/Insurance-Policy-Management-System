package com.policyhub.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.policyhub.entity.Customer;
import com.policyhub.entity.Policy;
import com.policyhub.entity.Premium;
import com.policyhub.entity.Premium.PremiumStatus;
import com.policyhub.repository.CustomerRepository;
import com.policyhub.repository.PolicyRepository;
import com.policyhub.repository.PremiumRepository;

@Service
public class PremiumService {
	@Autowired
	private PremiumRepository premiumRepository;
	@Autowired
	private CustomerRepository customerRepository;
	@Autowired
	private PolicyRepository policyRepository;

	public BigDecimal calculatePremium(Policy policy) {
		double baseRate;
		String pt = policy.getPolicyType() == null ? "" : policy.getPolicyType().toLowerCase();
		switch (pt) {
		case "life insurance":
			baseRate = 0.8;
			break;
		case "health insurance":
			baseRate = 1.0;
			break;
		case "car insurance":
			baseRate = 1.2;
			break;
		case "home insurance":
			baseRate = 0.6;
			break;
		case "travel insurance":
			baseRate = 1.5;
			break;
		default:
			baseRate = 1.0;
			break;
		}
		int duration = policy.getDurationYears() == null ? 1 : policy.getDurationYears();
		return policy.getCoverageAmount()
				.multiply(BigDecimal.valueOf(baseRate))
				.multiply(BigDecimal.valueOf(duration))
				.divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
	}

	public Premium generatePremium(Integer customerId, Integer policyId) {
		Customer customer = customerRepository.findById(customerId)
				.orElseThrow(() -> new IllegalArgumentException("Customer not found"));
		
		Policy policy = policyRepository.findById(policyId)
				.orElseThrow(() -> new IllegalArgumentException("Policy not found"));
		
		BigDecimal amount = calculatePremium(policy);
		Premium p = new Premium();
		p.setCustomer(customer);
		p.setPolicy(policy);
		p.setAmount(amount);
		p.setStatus(PremiumStatus.PENDING);
		p.setDueDate(LocalDate.now().plusMonths(1));
		return premiumRepository.save(p);
	}

	public Premium markPaid(Integer premiumId) {
		Premium p = premiumRepository.findById(premiumId)
				.orElseThrow(() -> new IllegalArgumentException("Premium not found"));
		
		p.setStatus(PremiumStatus.PAID);
		return premiumRepository.save(p);
	}

	public List<Premium> getByCustomer(Integer customerId) {
		return premiumRepository.findByCustomer_CustomerId(customerId);
	}

	public List<Premium> getAllPremiums() {
		return premiumRepository.findAll();
	}
}
