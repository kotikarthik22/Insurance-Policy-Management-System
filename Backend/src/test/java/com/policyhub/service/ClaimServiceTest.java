package com.policyhub.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.policyhub.entity.Claim;
import com.policyhub.entity.Claim.ClaimStatus;
import com.policyhub.entity.Customer;
import com.policyhub.entity.Policy;
import com.policyhub.repository.ClaimRepository;
import com.policyhub.repository.CustomerRepository;
import com.policyhub.repository.PolicyRepository;

@ExtendWith(MockitoExtension.class)
public class ClaimServiceTest {

    @Mock
    private ClaimRepository claimRepository;
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private PolicyRepository policyRepository;

    @InjectMocks
    private ClaimService claimService;

    @Test
    public void testCreateClaim_Success() {
        Integer customerId = 1;
        Integer policyId = 1;
        Customer customer = new Customer();
        customer.setCustomerId(customerId);
        Policy policy = new Policy();
        policy.setPolicyId(policyId);
        Claim claim = new Claim();
        claim.setClaimAmount(BigDecimal.valueOf(1000.00));

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(policyRepository.findById(policyId)).thenReturn(Optional.of(policy));
        when(claimRepository.save(any(Claim.class))).thenAnswer(invocation -> {
            Claim savedClaim = invocation.getArgument(0);
            savedClaim.setClaimId(1);
            return savedClaim;
        });

        Claim result = claimService.createClaim(customerId, policyId, claim);

        assertNotNull(result);
        assertEquals(ClaimStatus.PENDING, result.getClaimStatus());
        assertEquals(customer, result.getCustomer());
        assertEquals(policy, result.getPolicy());
        assertEquals(BigDecimal.valueOf(1000.00), result.getClaimAmount());
        verify(claimRepository).save(any(Claim.class));
    }

    @Test
    public void testCreateClaim_InvalidAmount() {
        Integer customerId = 1;
        Integer policyId = 1;
        Claim claim = new Claim();
        claim.setClaimAmount(BigDecimal.valueOf(-100.00));

        assertThrows(IllegalArgumentException.class, () -> claimService.createClaim(customerId, policyId, claim));
    }

    @Test
    public void testUpdateStatus() {
        Integer claimId = 1;
        Claim claim = new Claim();
        claim.setClaimId(claimId);
        claim.setClaimStatus(ClaimStatus.PENDING);

        when(claimRepository.findById(claimId)).thenReturn(Optional.of(claim));
        when(claimRepository.save(any(Claim.class))).thenReturn(claim);

        Claim result = claimService.updateStatus(claimId, ClaimStatus.APPROVED);

        assertEquals(ClaimStatus.APPROVED, result.getClaimStatus());
        assertNotNull(result.getSettlementDate());
        verify(claimRepository).save(claim);
    }
}
