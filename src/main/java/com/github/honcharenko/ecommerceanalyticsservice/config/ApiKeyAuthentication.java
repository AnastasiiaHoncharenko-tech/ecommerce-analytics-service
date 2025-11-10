package com.github.honcharenko.ecommerceanalyticsservice.config;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Collections;

public class ApiKeyAuthentication implements Authentication {

    private final String apiKey;
    private final boolean authenticated;

    public ApiKeyAuthentication(String apiKey, boolean authenticated1) {
        this.apiKey = apiKey;
        this.authenticated = authenticated1;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getDetails() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return apiKey;
    }

    @Override
    public boolean isAuthenticated() {
        return authenticated;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {

    }

    @Override
    public String getName() {
        return apiKey;
    }
}
