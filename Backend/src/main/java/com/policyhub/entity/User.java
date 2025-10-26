package com.policyhub.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Entity
@Table(name = "users")
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer userId;

    @NotBlank
    @Column(nullable = false, unique = true)
    @Pattern(
        regexp = "^[A-Za-z][A-Za-z0-9_]{4,15}$",
        message = "Username must be 5–16 characters"
        )
    private String username;

    @NotBlank
    @Column(nullable = false)
    private String password;
    
    public enum Role{ ADMIN ,CUSTOMER }

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;

    public User() {}
    public User(String username, String password, Role role) {
        this.username = username; this.password = password; this.role = role;
    }

    // getters & setters
    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
}
