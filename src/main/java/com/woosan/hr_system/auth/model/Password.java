package com.woosan.hr_system.auth.model;

import java.time.LocalDateTime;

public class Password {
    private String employeeId;
    private String password;
    private int passwordCount;
    private LocalDateTime lastModified;
    private String modifiedBy;
    private int strength;

    public String getEmployeeId() { return employeeId; }

    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) { this.password = password; }

    public int getPasswordCount() { return passwordCount; }

    public void setPasswordCount(int passwordCnt) { this.passwordCount = passwordCnt; }

    public LocalDateTime getLastModified() {
        return lastModified;
    }

    public void setLastModified(LocalDateTime lastModified) {
        this.lastModified = lastModified;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public int getStrength() { return strength; }

    public void setStrength(int strength) { this.strength = strength; }
}
