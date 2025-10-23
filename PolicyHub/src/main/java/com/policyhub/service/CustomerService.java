package com.policyhub.service;

import java.util.List;
import java.util.Optional;
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
    @Autowired private CustomerRepository customerRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private PolicyRepository policyRepository;
    @Autowired private PremiumService premiumService;

    public Customer createCustomer(Customer customer) {
        if (customer.getUser() == null || customer.getUser().getUserId() == null) {
            throw new IllegalArgumentException("User id required in customer.user.userId");
        }
        Optional<User> u = userRepository.findById(customer.getUser().getUserId());
        if (u.isEmpty()) throw new IllegalArgumentException("User not found");
        customer.setUser(u.get());
        return customerRepository.save(customer);
    }

    public Customer getByUserId(Integer userId) {
        return customerRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));
    }

    public Customer updateProfile(Integer userId, Customer updated) {
        Customer existing = getByUserId(userId);
        if (updated.getFullName() != null) existing.setFullName(updated.getFullName());
        if (updated.getEmail() != null) existing.setEmail(updated.getEmail());
        if (updated.getPhone() != null) existing.setPhone(updated.getPhone());
        if (updated.getAddress() != null) existing.setAddress(updated.getAddress());
        return customerRepository.save(existing);
    }

    public void buyPolicy(Integer customerId, Integer policyId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));
        Policy policy = policyRepository.findById(policyId)
                .orElseThrow(() -> new IllegalArgumentException("Policy not found"));
        // add relation
        customer.addPolicy(policy);
        customerRepository.save(customer);
        // generate premium
        premiumService.generatePremium(customerId, policyId);
    }

    public List<Customer> getAllCustomers() { return customerRepository.findAll(); }
}

