package com.example.demo.security;

import com.example.demo.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class CustomOAuth2User implements OAuth2User {
    private final Map<String, Object> attributes;
    private final Role role;

    public CustomOAuth2User(Map<String, Object> attributes, Role role) {
        this.attributes = attributes;
        this.role = role;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getName() {
        return attributes.get("email").toString();
    }

    public Role getRole() {
        return role;
    }
}