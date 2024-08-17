package com.woosan.hr_system.salary.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Salary { // 급여 정보
    private int salaryId;          // PK
    private String employeeId;     // FK, employees 테이블 참조
    private int annualSalary;      // 연봉
    private String bank;           // 은행명
    private String accountNumber;  // 계좌번호
}
