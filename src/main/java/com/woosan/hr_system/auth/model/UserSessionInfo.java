package com.woosan.hr_system.auth.model;

import lombok.Getter;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;

@Getter
public class UserSessionInfo {
    private String currentEmployeeId;
    private LocalDateTime now;

    public UserSessionInfo() {
        this.currentEmployeeId = SecurityContextHolder.getContext().getAuthentication().getName();
        this.now = LocalDateTime.now();
    }
}
