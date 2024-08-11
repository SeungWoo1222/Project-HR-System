package com.woosan.hr_system.employee.model;

import com.woosan.hr_system.auth.model.Password;
import com.woosan.hr_system.resignation.model.Resignation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@AllArgsConstructor
@NoArgsConstructor
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

    public void registerNewEmployee(Employee employee, String employeeId) {
        this.employeeId = employeeId;
        this.name = employee.getName();
        this.birth = employee.getBirth();
        this.residentRegistrationNumber = employee.getResidentRegistrationNumber();
        this.phone = employee.getPhone();
        this.email = employee.getEmail();
        this.address = employee.getAddress();
        this.detailAddress = employee.getDetailAddress();
        this.department = employee.getDepartment();
        this.position = employee.getPosition();
        this.hireDate = employee.getHireDate();
        this.status = "재직";
        this.remainingLeave = 11; // 기본 연차 11일 설정
        this.picture = employee.getPicture();
    }

    // 사원 아이디 생성
    public String createEmployeeId(Employee employee, int currentYearEmpolyeesCount) {
        // 형식 : AABBCCC (부서 코드, 입사년도, 해당 년도 입사 순서)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String BB = employee.getHireDate().format(formatter).substring(2, 4);
        String CCC = String.format("%03d", currentYearEmpolyeesCount + 1);
        return employee.getDepartment() + BB + CCC;
    }

    // 사진 FileId 할당
    public void assignPicture(int fileId) {
        if (fileId <= 0) {
            throw new IllegalArgumentException("유효하지 않은 파일 ID입니다.");
        }
        this.picture = fileId;
    }
}
