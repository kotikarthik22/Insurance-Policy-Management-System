package com.policyhub.service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.policyhub.entity.Customer;
import com.policyhub.entity.Policy;
import com.policyhub.entity.User;
import com.policyhub.repository.CustomerRepository;
import com.policyhub.repository.PolicyRepository;
import com.policyhub.repository.UserRepository;

@Service
public class CustomerService {
	@Autowired
	private CustomerRepository customerRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private PolicyRepository policyRepository;
	@Autowired
	private PremiumService premiumService;

	private static Logger log = LoggerFactory.getLogger(CustomerService.class);

	public Customer createCustomer(Customer customer) {

		log.info("Creating new customer: {}", customer.getFullName());

		if (customer.getUser() == null || customer.getUser().getUserId() == null) {

			log.error("User ID is missing while creating customer");
			throw new IllegalArgumentException("User id required in customer.user.userId");
		}
		Optional<User> u = userRepository.findById(customer.getUser().getUserId());

		if (u.isEmpty())
			throw new IllegalArgumentException("User not found");
		customer.setUser(u.get());
		return customerRepository.save(customer);
	}

	public Customer getByUserId(Integer userId) {
		return customerRepository.findByUser_UserId(userId)
				.orElseThrow(() -> new IllegalArgumentException("Customer not found"));
	}

	public Customer updateProfile(Integer userId, Customer updated) {
		Customer existing = getByUserId(userId);
		if (updated.getFullName() != null)
			existing.setFullName(updated.getFullName());
		if (updated.getEmail() != null)
			existing.setEmail(updated.getEmail());
		if (updated.getPhone() != null)
			existing.setPhone(updated.getPhone());
		if (updated.getAddress() != null)
			existing.setAddress(updated.getAddress());
		return customerRepository.save(existing);
	}

	public void buyPolicy(Integer customerId, Integer policyId) {
		log.info("Customer {} attempting to buy policy {}", customerId, policyId);

		Customer customer = customerRepository.findById(customerId)
				.orElseThrow(() -> new IllegalArgumentException("Customer not found"));
		Policy policy = policyRepository.findById(policyId)
				.orElseThrow(() -> new IllegalArgumentException("Policy not found"));
		customer.addPolicy(policy);
		customerRepository.save(customer);
		premiumService.generatePremium(customerId, policyId);
		
		log.info("Customer {} successfully purchased policy {}",customerId,policyId);
	}

	public List<Customer> getAllCustomers() {
		return customerRepository.findAll();
	}
}
