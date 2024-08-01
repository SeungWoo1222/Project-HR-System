package com.woosan.hr_system.employee.model;

import com.woosan.hr_system.auth.model.Password;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class Employee {
    private String employeeId;
    private String name;
    private String birth;
    private String residentRegistrationNumber;
    private String phone;
    private String email;
    private String address;
    private String detailAddress;
    private Department department;
    private Position position;
    private LocalDate hireDate;
    private String status;
    private String picture;
    private int remainingLeave;
    private LocalDateTime lastModified;
    private String modifiedBy;
    private Resignation resignation;
    private Password password;
}
