package com.policyhub.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.policyhub.entity.Policy;

@Repository
public interface PolicyRepository extends JpaRepository<Policy, Integer> {
}
