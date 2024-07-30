package com.woosan.hr_system.auth.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class CustomUserDetails implements UserDetails {

    private static final long serialVersionUID = 1L;

    private final String username;
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;
    private final String department;
    private final boolean isAccountNonLocked;
    private final boolean isAccountNonExpired;

    public CustomUserDetails(String username, String password, Collection<? extends GrantedAuthority> authorities, String department, boolean isAccountNonLocked, boolean isAccountNonExpired) {
        this.username = username;
        this.password = password;
        this.authorities = authorities;
        this.department = department;
        this.isAccountNonLocked = isAccountNonLocked;
        this.isAccountNonExpired = isAccountNonExpired;
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

    @Override // 계정 만료 여부
    public boolean isAccountNonExpired() {
        return isAccountNonExpired;
    }

    @Override // 계정 잠금 여부
    public boolean isAccountNonLocked() {
        return isAccountNonLocked;
    }

    @Override // 자격 증명 만료 여부
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override // 계정 활성화 여부
    public boolean isEnabled() {
        return true;
    }
}

