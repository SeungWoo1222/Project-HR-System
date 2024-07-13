package com.woosan.hr_system.auth;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class CustomUserDetails implements UserDetails {
    private static final long serialVersionUID = 1L;

    private final String username;
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;
    private final String department;

    public CustomUserDetails(String username, String password, Collection<? extends GrantedAuthority> authorities, String department) {
        this.username = username;
        this.password = password;
        this.authorities = authorities;
        this.department = department;
    }

    public String getDepartment() {
        return department;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}

