package com.github.honcharenko.ecommerceanalyticsservice.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class ApiKeyAuthFilter extends OncePerRequestFilter {

    private final String correctApiKey;

    public ApiKeyAuthFilter(String correctApiKey) {
        this.correctApiKey = correctApiKey;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String providedKey = request.getHeader("X-API-KEY");

        if (providedKey != null && providedKey.equals(correctApiKey)) {
            var auth = new ApiKeyAuthentication(providedKey, true);
            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        filterChain.doFilter(request, response);
    }
}
