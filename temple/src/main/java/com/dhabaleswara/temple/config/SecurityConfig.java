package com.dhabaleswara.temple.config;

import com.dhabaleswara.temple.service.LoginAttemptService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public AuthenticationSuccessHandler successHandler(LoginAttemptService attempts) {
        SavedRequestAwareAuthenticationSuccessHandler h = new SavedRequestAwareAuthenticationSuccessHandler();
        h.setDefaultTargetUrl("/admin");
        return (req, res, auth) -> {
            attempts.succeeded(auth.getName());
            h.onAuthenticationSuccess(req, res, auth);
        };
    }

    @Bean
    public AuthenticationFailureHandler failureHandler(LoginAttemptService attempts) {
        SimpleUrlAuthenticationFailureHandler h = new SimpleUrlAuthenticationFailureHandler("/login?error");
        return (req, res, ex) -> {
            String u = req.getParameter("username");
            if (u != null) attempts.failed(u);
            h.onAuthenticationFailure(req, res, ex);
        };
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           AuthenticationSuccessHandler success,
                                           AuthenticationFailureHandler failure) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/enquiries").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/rituals", "/api/festivals").permitAll()
                .requestMatchers("/h2-console/**").denyAll()
                .anyRequest().permitAll())          // the public temple website
            .formLogin(f -> f
                .loginPage("/login")
                .successHandler(success)
                .failureHandler(failure)
                .permitAll())
            .logout(l -> l
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout", "POST"))
                .logoutSuccessUrl("/login?logout")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID"))
            // Public JSON endpoint is called by the static site's JS, so it is exempt from CSRF.
            // Every admin form keeps CSRF protection (Thymeleaf adds the token automatically).
            .csrf(c -> c.ignoringRequestMatchers("/api/enquiries"))
            .sessionManagement(s -> s
                .sessionFixation(f -> f.migrateSession()))
            .headers(h -> h
                .contentTypeOptions(Customizer.withDefaults())
                .frameOptions(f -> f.deny())
                .referrerPolicy(r -> r.policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN))
                .httpStrictTransportSecurity(hsts -> hsts.includeSubDomains(true).maxAgeInSeconds(31536000)));
        return http.build();
    }
}
