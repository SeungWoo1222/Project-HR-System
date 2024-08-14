package com.woosan.hr_system.salary.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class PayrollDetails {
    private int annualSalary;               // 연봉
    // 고정 수당
    private double baseSalaryRatio;         // 기본급 비율 (70%)
    private double positionAllowanceRatio;  // 직책 수당 비율 (10%)
    private int mealAllowanceRatio;         // 식대 비율 (5%) - 비과세
    private int transportAllowanceRatio;    // 교통비 비율 (5%) - 비과세
    // 성과급
    private double personalBonusRatio;      // 개인 성과급 비율 (2.5%)
    private double teamBonusRatio;          // 팀 성과급 비율 (2.5%)
    // 보너스
    private double holidayBonusRatio;       // 명절 보너스 비율 (2.5%)
    private double yearEndBonusRatio;       // 연말 보너스 비율 (2.5%)

    // 행위 중심 메소드


    // 비율에 따른 계산 메서드
    private int calculate(double ratio) {
        return (int) (annualSalary * (ratio / 100));
    }

    public int calculateBaseSalary() {
        return calculate(baseSalaryRatio);
    }

    public int calculatePositionAllowance() {
        return calculate(positionAllowanceRatio);
    }

    public int calculateMealAllowance() {
        return calculate(mealAllowanceRatio);
    }

    public int calculateTransportAllowance() {
        return calculate(transportAllowanceRatio);
    }

    public int calculatePersonalBonus() {
        return calculate(personalBonusRatio);
    }

    public int calculateTeamBonus() {
        return calculate(teamBonusRatio);
    }

    public int calculateHolidayBonus() {
        return calculate(holidayBonusRatio);
    }

    public int calculateYearEndBonus() {
        return calculate(yearEndBonusRatio);
    }

    public int calculateTotalPayrolls() {
        return calculateBaseSalary()
                + calculatePositionAllowance()
                + calculateMealAllowance()
                + calculateTransportAllowance()
                + calculatePersonalBonus()
                + calculateTeamBonus()
                + calculateHolidayBonus()
                + calculateYearEndBonus();
    }
}
