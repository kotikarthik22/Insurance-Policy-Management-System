package com.policyhub.service;

import java.time.LocalDate;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.policyhub.entity.Claim;
import com.policyhub.entity.Claim.ClaimStatus;
import com.policyhub.entity.Customer;
import com.policyhub.entity.Policy;
import com.policyhub.repository.ClaimRepository;
import com.policyhub.repository.CustomerRepository;
import com.policyhub.repository.PolicyRepository;

@Service
public class ClaimService {
	@Autowired
	private ClaimRepository claimRepository;
	@Autowired
	private CustomerRepository customerRepository;
	@Autowired
	private PolicyRepository policyRepository;
	
	private static Logger log = LoggerFactory.getLogger(ClaimService.class);


	public Claim createClaim(Integer customerId, Integer policyId, Claim claim) {
		
		Customer customer = customerRepository.findById(customerId)
				.orElseThrow(() -> new IllegalArgumentException("Customer not found"));

		Policy policy = policyRepository.findById(policyId)
				.orElseThrow(() -> new IllegalArgumentException("Policy not found"));

		if (claim.getClaimAmount() == null || claim.getClaimAmount().signum() <= 0) {
			log.error("Invalid claim amount");
			
			throw new IllegalArgumentException("Invalid claim amount");
		}

		if (policy.getCoverageAmount() != null && claim.getClaimAmount() != null
				&& claim.getClaimAmount().compareTo(policy.getCoverageAmount()) > 0) {
			
			log.error("Claim Amount should not Exceed Policy Coverage Amount");
			throw new IllegalArgumentException("Claim Amount should not Exceed Policy Coverage Amount");

		}

		claim.setCustomer(customer);
		claim.setPolicy(policy);
		claim.setClaimStatus(ClaimStatus.PENDING);
		claim.setSubmissionDate(LocalDate.now());
		return claimRepository.save(claim);
	}

	public Claim updateStatus(Integer claimId, ClaimStatus status) {
		Claim c = claimRepository.findById(claimId).orElseThrow(() -> new IllegalArgumentException("Claim not found"));
		c.setClaimStatus(status);
		if (status == ClaimStatus.APPROVED || status == ClaimStatus.REJECTED) {
			c.setSettlementDate(LocalDate.now());
		}

		return claimRepository.save(c);
	}

	public Claim getClaim(Integer claimId) {
		return claimRepository.findById(claimId).orElseThrow(() -> new IllegalArgumentException("Claim not found"));
	}

	public List<Claim> getByCustomer(Integer customerId) {
		return claimRepository.findByCustomer_CustomerId(customerId);
	}

	public List<Claim> getAll() {
		return claimRepository.findAll();
	}
}
