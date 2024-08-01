package com.woosan.hr_system.auth.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Password {
    private String employeeId;
    private String password;
    private int passwordCount;
    private LocalDateTime lastModified;
    private String modifiedBy;
    private int strength;
}
