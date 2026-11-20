package com.dhabaleswara.temple.service;

import com.dhabaleswara.temple.model.AdminUser;
import com.dhabaleswara.temple.repository.AdminUserRepository;
import java.time.LocalDateTime;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
public class AdminUserDetailsService implements UserDetailsService {
    private final AdminUserRepository repo;

    public AdminUserDetailsService(AdminUserRepository repo) { this.repo = repo; }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AdminUser u = repo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Not found"));
        boolean locked = u.getLockedUntil() != null && u.getLockedUntil().isAfter(LocalDateTime.now());
        return User.withUsername(u.getUsername())
                .password(u.getPassword())
                .roles(u.getRole())
                .disabled(!u.isEnabled())
                .accountLocked(locked)
                .build();
    }
}
