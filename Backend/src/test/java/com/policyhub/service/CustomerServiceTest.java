package com.policyhub.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.policyhub.entity.Customer;
import com.policyhub.entity.User;
import com.policyhub.repository.CustomerRepository;
import com.policyhub.repository.PolicyRepository;
import com.policyhub.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PolicyRepository policyRepository;
    @Mock
    private PremiumService premiumService;

    @InjectMocks
    private CustomerService customerService;

    @Test
    public void testCreateCustomer_Success() {
        User user = new User();
        user.setUserId(1);
        Customer customer = new Customer();
        customer.setUser(user);

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(customerRepository.save(any(Customer.class))).thenAnswer(invocation -> {
            Customer savedCustomer = invocation.getArgument(0);
            savedCustomer.setCustomerId(1);
            return savedCustomer;
        });

        Customer result = customerService.createCustomer(customer);

        assertNotNull(result);
        assertEquals(user, result.getUser());
        verify(customerRepository).save(customer);
    }

    @Test
    public void testCreateCustomer_UserNotFound() {
        Customer customer = new Customer();
        customer.setUser(new User());
        customer.getUser().setUserId(1);

        when(userRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> customerService.createCustomer(customer));
    }

    @Test
    public void testGetByUserId() {
        Integer userId = 1;
        Customer customer = new Customer();
        customer.setCustomerId(1);

        when(customerRepository.findByUser_UserId(userId)).thenReturn(Optional.of(customer));

        Customer result = customerService.getByUserId(userId);

        assertEquals(customer, result);
    }
}
