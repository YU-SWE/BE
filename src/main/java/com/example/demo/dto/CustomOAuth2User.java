package com.example.demo.dto;

import com.example.demo.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class CustomOAuth2User implements OAuth2User {

    private final OAuth2Response oAuth2Response;
    private final Role role;

    public CustomOAuth2User(OAuth2Response oAuth2Response, Role role){
        this.oAuth2Response = oAuth2Response;
        this.role = role;
//        this.role = role != null ? role : Role.USER; // Null이면 기본 값 설정
    }
    @Override
    public Map<String, Object> getAttributes() {

        return null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collection = new ArrayList<>();
        collection.add(new GrantedAuthority() {

            @Override
            public String getAuthority() {

                return role.getRole();
            }
        });
        return collection;
    }

    @Override
    public String getName() {

        return oAuth2Response.getName();
    }

    public String getUsername(){
        return oAuth2Response.getProvider()+ " " + oAuth2Response.getProviderId();
    }
}
