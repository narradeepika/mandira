package com.dhabaleswara.temple.service;

import com.dhabaleswara.temple.repository.AdminUserRepository;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/** Locks an account for a while after repeated failed logins (brute-force protection). */
@Service
public class LoginAttemptService {
    private final AdminUserRepository repo;
    private final int maxAttempts;
    private final int lockMinutes;

    public LoginAttemptService(AdminUserRepository repo,
                               @Value("${app.security.max-failed-attempts:5}") int maxAttempts,
                               @Value("${app.security.lock-minutes:15}") int lockMinutes) {
        this.repo = repo; this.maxAttempts = maxAttempts; this.lockMinutes = lockMinutes;
    }

    public void failed(String username) {
        repo.findByUsername(username).ifPresent(u -> {
            int n = u.getFailedAttempts() + 1;
            u.setFailedAttempts(n);
            if (n >= maxAttempts) {
                u.setLockedUntil(LocalDateTime.now().plusMinutes(lockMinutes));
                u.setFailedAttempts(0);
            }
            repo.save(u);
        });
    }

    public void succeeded(String username) {
        repo.findByUsername(username).ifPresent(u -> {
            if (u.getFailedAttempts() != 0 || u.getLockedUntil() != null) {
                u.setFailedAttempts(0);
                u.setLockedUntil(null);
                repo.save(u);
            }
        });
    }
}
