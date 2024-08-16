package com.woosan.hr_system.salary.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class DeductionDetails {
    private int taxableSalary;              // 비과세 미포함 월급
    private int incomeTax;                  // 근로소득세 (소득세율표에 따라 설정) (1인 가구 기준, 공제율 100%)
    private double localIncomeTaxRate;      // 지방소득세율 (소득세의 10%)
    private double nationalPensionRate;     // 국민연금 비율 (4.5%)
    private double healthInsuranceRate;     // 건강보험 비율 (3.545%)
    private double longTermCareRate;        // 장기요양보험 비율 (0.4591%)
    private double employmentInsuranceRate; // 고용보험 비율 (0.9%)

    // 행위 중심 메소드
    private int calculate(double ratio, int income) {
        return (int) (income * (ratio / 100));
    }

    public int calculateIncomeTax() {
        return 0;
    }

    public int calculateLocalIncomeTax() {
        return incomeTax * calculate(localIncomeTaxRate,incomeTax);
    }

    public int calculateNationalPension() {
        return calculate(nationalPensionRate, taxableSalary);
    }

    public int calculateHealthInsurance() {
        return calculate(healthInsuranceRate, taxableSalary);
    }

    public int calculateLongTermCareInsurance() {
        return calculate(longTermCareRate, taxableSalary);
    }

    public int calculateEmploymentInsurance() {
        return calculate(employmentInsuranceRate, taxableSalary);
    }

    public int calculateTotalDeductions() {
        return incomeTax
                + calculateLocalIncomeTax()
                + calculateNationalPension()
                + calculateHealthInsurance()
                + calculateLongTermCareInsurance()
                + calculateEmploymentInsurance();
    }
}
