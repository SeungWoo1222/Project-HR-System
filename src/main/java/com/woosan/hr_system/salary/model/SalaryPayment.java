package com.woosan.hr_system.salary.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SalaryPayment { // 급여 명세서
    private int paymentId;             // PK
    private int salaryId;              // FK, salaries 테이블 참조
    private Date paymentDate;          // 급여 지급일

    // 급여 구성 항목
    private int baseSalary;            // 기본급
    private int positionAllowance;     // 직책 수당
    private int mealAllowance;         // 식대 (비과세)
    private int transportAllowance;    // 교통비 (비과세)

    // 보너스 항목
    private int personalBonus;         // 개인 성과급
    private int teamBonus;             // 팀 성과급
    private int holidayBonus;          // 명절 보너스
    private int yearEndBonus;          // 연말 보너스

    // 연장 수당 항목
    private int overtimePay;           // 연장 근무 수당

    // 공제 항목
    private int incomeTax;             // 근로소득세
    private int localIncomeTax;        // 지방소득세
    private int nationalPension;       // 국민연금
    private int healthInsurance;       // 건강보험
    private int longTermCareInsurance; // 장기요양보험
    private int employmentInsurance;   // 고용보험

    private int deductions;            // 총 공제 금액 (위 공제 항목들의 합)
    private int grossSalary;           // 총 급여
    private int netSalary;             // 실 지급액 (총 급여 - 공제 금액)
    private String remarks;            // 비고
}
