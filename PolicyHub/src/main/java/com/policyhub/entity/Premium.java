package com.policyhub.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "premiums")
public class Premium {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "premium_id")
    private Integer premiumId;

    @NotNull
    @Column(name = "amount", precision = 12, scale = 2)
    private BigDecimal amount;
    
    public enum PremiumStatus{PENDING,PAID,OVERDUE}

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    private PremiumStatus status = PremiumStatus.PENDING;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @ManyToOne
    @JoinColumn(name = "policy_id", nullable = false)
    private Policy policy;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    public Premium() {}

	public Integer getPremiumId() {
		return premiumId;
	}

	public void setPremiumId(Integer premiumId) {
		this.premiumId = premiumId;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public PremiumStatus getStatus() {
		return status;
	}

	public void setStatus(PremiumStatus status) {
		this.status = status;
	}

	public LocalDate getDueDate() {
		return dueDate;
	}

	public void setDueDate(LocalDate dueDate) {
		this.dueDate = dueDate;
	}

	public Policy getPolicy() {
		return policy;
	}

	public void setPolicy(Policy policy) {
		this.policy = policy;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}
    
    
    
    

    }
