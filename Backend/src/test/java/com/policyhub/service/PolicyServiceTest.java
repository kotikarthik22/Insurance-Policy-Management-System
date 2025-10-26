package com.policyhub.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.policyhub.entity.Policy;
import com.policyhub.repository.PolicyRepository;

@ExtendWith(MockitoExtension.class)
public class PolicyServiceTest {

    @Mock
    private PolicyRepository policyRepository;
    @Mock
    private PremiumService premiumService;

    @InjectMocks
    private PolicyService policyService;

    @Test
    public void testCreatePolicy() {
        Policy policy = new Policy();
        policy.setPolicyType("Life Insurance");
        policy.setCoverageAmount(BigDecimal.valueOf(100000.00));
        policy.setDurationYears(10);

        when(premiumService.calculatePremium(policy)).thenReturn(BigDecimal.valueOf(8000.00));
        when(policyRepository.save(any(Policy.class))).thenAnswer(invocation -> {
            Policy savedPolicy = invocation.getArgument(0);
            savedPolicy.setPolicyId(1);
            return savedPolicy;
        });

        Policy result = policyService.createPolicy(policy);

        assertNotNull(result);
        assertEquals(BigDecimal.valueOf(8000.00), result.getPremiumAmount());
        verify(policyRepository).save(policy);
    }

    @Test
    public void testGetPolicy() {
        Integer policyId = 1;
        Policy policy = new Policy();
        policy.setPolicyId(policyId);

        when(policyRepository.findById(policyId)).thenReturn(java.util.Optional.of(policy));

        Policy result = policyService.getPolicy(policyId);

        assertEquals(policy, result);
    }

    @Test
    public void testUpdatePolicy() {
        Integer policyId = 1;
        Policy existing = new Policy();
        existing.setPolicyId(policyId);
        existing.setPolicyType("Old Type");
        existing.setCoverageAmount(BigDecimal.valueOf(50000.00));
        existing.setDurationYears(5);

        Policy updated = new Policy();
        updated.setPolicyType("New Type");
        updated.setCoverageAmount(BigDecimal.valueOf(100000.00));
        updated.setDurationYears(10);

        when(policyRepository.findById(policyId)).thenReturn(java.util.Optional.of(existing));
        when(premiumService.calculatePremium(existing)).thenReturn(BigDecimal.valueOf(8000.00));
        when(policyRepository.save(any(Policy.class))).thenReturn(existing);

        Policy result = policyService.updatePolicy(policyId, updated);

        assertEquals("New Type", result.getPolicyType());
        assertEquals(BigDecimal.valueOf(100000.00), result.getCoverageAmount());
        assertEquals(10, result.getDurationYears());
        verify(policyRepository).save(existing);
    }
}
