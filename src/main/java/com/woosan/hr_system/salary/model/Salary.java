package com.woosan.hr_system.salary.model;

import com.woosan.hr_system.employee.model.Department;
import com.woosan.hr_system.employee.model.Position;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Salary { // 급여 정보
    private int salaryId;          // PK
    private String employeeId;     // FK, employees 테이블 참조
    private Department department; // 부서
    private Position position;     // 직급
    private String bank;           // 은행명
    private String accountNumber;  // 계좌번호
    private int annualSalary;      // 연봉
    private LocalDate createdAt;   // 등록된 날짜
    private Boolean status;        // 사용 여부
}
