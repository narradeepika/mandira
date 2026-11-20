package com.dhabaleswara.temple.config;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/** Simple per-IP limiter: enquiries (5 / 10 min) and login attempts (10 / 10 min). */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RateLimitFilter implements Filter {
    private static final long WINDOW_MS = 10 * 60 * 1000L;
    private final ConcurrentHashMap<String, Deque<Long>> hits = new ConcurrentHashMap<>();

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest r = (HttpServletRequest) req;
        HttpServletResponse w = (HttpServletResponse) res;
        if ("POST".equals(r.getMethod())) {
            String path = r.getRequestURI();
            int limit = path.equals("/api/enquiries") ? 5 : path.equals("/login") ? 10 : 0;
            if (limit > 0 && !allow(r.getRemoteAddr() + path, limit)) {
                w.setStatus(429);
                w.setContentType("application/json");
                w.getWriter().write("{\"error\":\"Too many requests. Please try again later.\"}");
                return;
            }
        }
        chain.doFilter(req, res);
    }

    private boolean allow(String key, int limit) {
        long now = System.currentTimeMillis();
        Deque<Long> q = hits.computeIfAbsent(key, k -> new ArrayDeque<>());
        synchronized (q) {
            while (!q.isEmpty() && now - q.peekFirst() > WINDOW_MS) q.pollFirst();
            if (q.size() >= limit) return false;
            q.addLast(now);
            return true;
        }
    }
}
