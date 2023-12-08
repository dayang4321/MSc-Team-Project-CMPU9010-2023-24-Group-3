package com.docparser.springboot.security;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

/*
 * Lombok annotation to generate getters, setters, toString, equals, and hashCode methods
 */
@Data
public class CustomUserDetails implements OAuth2User, UserDetails {

    // User-specific fields
    private String id;
    private String username;
    private String password;
    private String name;
    private String email;
    private String avatarUrl;
    private String provider;
    private Collection<? extends GrantedAuthority> authorities;
    private Map<String, Object> attributes;

    // Methods from UserDetails interface ensuring compliance with Spring Security

    // Check if the account is not set to expire
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    // Check if the account is not locked
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    // Check if the credentials (e.g., password) are not expired
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    // Check if the account is enabled
    @Override
    public boolean isEnabled() {
        return true;
    }
}
