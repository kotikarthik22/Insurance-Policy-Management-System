package com.policyhub.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "customers")
public class Customer {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "customer_id")
    private Integer customerId;

    @NotBlank(message = "Customer name is required")
    @Pattern(regexp = "^[A-Za-z ]+$",message = "Only Alphabets Allowed")
	 private String fullName;
    
	 @NotBlank(message = "Email is required")
	 @Email(message = "Invalid email")
	 private String email;
	 
	 @NotBlank(message = "Contact number is required")
	 @Size(min = 10, max = 10, message = "Contact Number must be 10 digits")
	 @Pattern(regexp = "\\d+",message = "Only Digits Allowed")
	 private String phone;	
	 
	 @NotBlank(message = "Address is required")
	 private String address;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", unique = true)
    private User user;

    @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinTable(name = "customer_policies",
            joinColumns = @JoinColumn(name = "customer_id"),
            inverseJoinColumns = @JoinColumn(name = "policy_id"))
    private Set<Policy> policies = new HashSet<>();

    public Customer() {}
    
    
    public Integer getCustomerId() {
		return customerId;
	}

	public void setCustomerId(Integer customerId) {
		this.customerId = customerId;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Set<Policy> getPolicies() {
		return policies;
	}

	public void setPolicies(Set<Policy> policies) {
		this.policies = policies;
	}
	// --- Helper Methods ---
    public void addPolicy(Policy policy) {
        this.policies.add(policy);
        policy.getCustomers().add(this);
    }

    public void removePolicy(Policy policy) {
        this.policies.remove(policy);
        policy.getCustomers().remove(this);
    }


   
}
