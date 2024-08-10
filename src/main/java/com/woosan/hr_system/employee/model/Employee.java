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
    private int remainingLeave;
    private LocalDateTime lastModified;
    private String modifiedBy;
    private int picture;
    private Resignation resignation;
    private Password password;

    // 사진 할당 - 행위 중심 메소드
    public void assignPicture(int fileId) {
        if (fileId <= 0) {
            throw new IllegalArgumentException("유효하지 않은 파일 ID입니다.");
        }
        this.picture = fileId;
    }
}
