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
    private int netMonthlySalary;           // 비과세 미포함 월급
    private int incomeTax;                  // 근로소득세 (소득세율표에 따라 설정) (1인 가구 기준, 공제율 100%)
    private double localIncomeTaxRate;      // 지방소득세율 (소득세의 10%)
    private double nationalPensionRate;     // 국민연금 비율 (4.5%)
    private double healthInsuranceRate;     // 건강보험 비율 (3.545%)
    private double longTermCareRate;        // 장기요양보험 비율 (0.4591%)
    private double employmentInsuranceRate; // 고용보험 비율 (0.9%)

    // 행위 중심 메소드
    private double calculate(double ratio) {
        return ratio / 100;
    }

    public void calculateIncomeTax() {
    }

    public double calculateLocalIncomeTax() {
        return incomeTax * calculate(localIncomeTaxRate);
    }

    public double calculateNationalPension() {
        return netMonthlySalary * calculate(nationalPensionRate);
    }

    public double calculateHealthInsurance() {
        return netMonthlySalary * calculate(healthInsuranceRate);
    }

    public double calculateLongTermCareInsurance() {
        return netMonthlySalary * calculate(longTermCareRate);
    }

    public double calculateEmploymentInsurance() {
        return netMonthlySalary * calculate(employmentInsuranceRate);
    }

    public double calculateTotalDeductions() {
        return incomeTax
                + calculateLocalIncomeTax()
                + calculateNationalPension()
                + calculateHealthInsurance()
                + calculateLongTermCareInsurance()
                + calculateEmploymentInsurance();
    }
}
