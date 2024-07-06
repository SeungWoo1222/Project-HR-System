package com.woosan.hr_system.employee.model;

import java.time.LocalDateTime;

public class Employee {
    private String employeeId;
    private String name;
    private String birth;
    private String residentRegistrationNumber;
    private String phone;
    private String email;
    private String address;
    private String detailedAddress;
    private LocalDateTime hireDate;
    private LocalDateTime terminationDate;
    private String status;
    private String departmentId;
    private int positionId;
    private int remainingLeave;
    private LocalDateTime lastModified;
    private String modifiedBy;

    // Getters and Setters
    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBirth() {
        return birth;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }

    public String getResidentRegistrationNumber() {
        return residentRegistrationNumber;
    }

    public void setResidentRegistrationNumber(String residentRegistrationNumber) {
        this.residentRegistrationNumber = residentRegistrationNumber;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDetailedAddress() {
        return detailedAddress;
    }

    public void setDetailedAddress(String detailedAddress) {
        this.detailedAddress = detailedAddress;
    }

    public LocalDateTime getHireDate() {
        return hireDate;
    }

    public void setHireDate(LocalDateTime hireDate) {
        this.hireDate = hireDate;
    }

    public LocalDateTime getTerminationDate() {
        return terminationDate;
    }

    public void setTerminationDate(LocalDateTime terminationDate) {
        this.terminationDate = terminationDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(String departmentId) {
        this.departmentId = departmentId;
    }

    public int getPositionId() {
        return positionId;
    }

    public void setPositionId(int positionId) {
        this.positionId = positionId;
    }

    public int getRemainingLeave() {
        return remainingLeave;
    }

    public void setRemainingLeave(int remainingLeave) {
        this.remainingLeave = remainingLeave;
    }


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
}