//package com.example.demo.config;
//
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.AuthorityUtils;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.stereotype.Component;
//
//import java.util.Collection;
//import java.util.List;
//
//public class UserDetailsImpl implements UserDetails {
//
//    private String username;
//    private String role;
//
//    public UserDetailsImpl(String username, String role) {
//        this.username = username;
//        this.role = role;
//    }
//
//    @Override
//    public Collection<? extends GrantedAuthority> getAuthorities() {
//        return AuthorityUtils.createAuthorityList(role);
//    }
//
//    @Override
//    public String getPassword() {
//        return null;
//    }
//
//    @Override
//    public String getUsername() {
//        return username;
//    }
//}
