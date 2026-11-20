package com.dhabaleswara.temple.model;

import jakarta.persistence.*;

@Entity
@Table(name = "admin_users")
public class AdminUser {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true, length = 60) private String username;
    @Column(nullable = false) private String password; // BCrypt hash
    @Column(nullable = false, length = 30) private String role = "ADMIN";
    private boolean enabled = true;
    private int failedAttempts = 0;
    private java.time.LocalDateTime lockedUntil;

    public Long getId() { return id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public int getFailedAttempts() { return failedAttempts; }
    public void setFailedAttempts(int failedAttempts) { this.failedAttempts = failedAttempts; }
    public java.time.LocalDateTime getLockedUntil() { return lockedUntil; }
    public void setLockedUntil(java.time.LocalDateTime lockedUntil) { this.lockedUntil = lockedUntil; }
}
