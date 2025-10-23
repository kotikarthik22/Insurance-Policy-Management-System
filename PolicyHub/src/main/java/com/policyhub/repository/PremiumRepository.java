package com.policyhub.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.policyhub.entity.Premium;

@Repository
public interface PremiumRepository extends JpaRepository<Premium, Integer> {
    List<Premium> findByCustomer_CustomerId(Integer customerId);
}

