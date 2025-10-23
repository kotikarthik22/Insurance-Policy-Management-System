package com.policyhub.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.policyhub.entity.Claim;
import com.policyhub.entity.Claim.ClaimStatus;

@Repository
public interface ClaimRepository extends JpaRepository<Claim, Integer> {
    List<Claim> findByCustomer_CustomerId(Integer customerId);
    List<Claim> findByClaimStatus(ClaimStatus status);
}


