package com.woosan.hr_system.salary.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SalaryPayment {
    private int paymentId;          // PK
    private int salaryId;           // FK, salaries 테이블 참조
    private Date paymentDate;       // 급여 지급일
    private int grossSalary;        // 총 급여
    private int baseSalary;         // 기본급
    private Double overtime;        // 연장 근무 시간
    private int bonus;              // 보너스
    private int deductions;         // 세금 공제
    private int netSalary;          // 실 지급액
    private String remarks;         // 비고
}
