package com.woosan.hr_system.salary.service;

import com.github.usingsky.calendar.KoreanLunarCalendar;
import com.woosan.hr_system.employee.dao.EmployeeDAO;
import com.woosan.hr_system.salary.dao.RatioDAO;
import com.woosan.hr_system.salary.model.DeductionDetails;
import com.woosan.hr_system.salary.model.PayrollDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class SalaryCalculationImpl implements SalaryCalculation {
    @Autowired
    private RatioDAO ratioDAO;

    @Autowired
    private EmployeeDAO employeeDAO;

    // 연 수익 -> 월 수익으로 계산
    private double computeMonthlyIncome(int income) {
        return income / 12.0;
    }

    // 1원 단위는 반올림 처리
    private int computeRoundIncome(double income) {
        return ((int)Math.round(income / 10.0)) * 10;
    }

    // 특정 수당을 계산하고 Map에 추가하는 메서드
    private void addComponent(Map<String, Integer> components, String key, double value) {
        components.put(key, computeRoundIncome(value));
    }

    // 급여 구성 항목(기본급, 직책수당, 식대, 교통비) 계산
    private Map<String, Integer> calculateSalaryComponents(int annualSalary) {
        // 급여 구성항목의 비율 불러오기
        PayrollDetails payrollRatios = ratioDAO.selectPayrollRatios();

        // 새로운 PayrollDetails 객체 생성
        PayrollDetails payrollDetails = payrollRatios.toBuilder()
                .annualSalary(annualSalary)
                .build();

        // 급여 구성 항목 비율로 계산하여 반환
        Map<String, Integer> components = new HashMap<>();
        addComponent(components, "baseSalary", computeMonthlyIncome(payrollDetails.calculateBaseSalary()));
        addComponent(components, "positionAllowance", computeMonthlyIncome(payrollDetails.calculatePositionAllowance()));
        addComponent(components, "mealAllowance", computeMonthlyIncome(payrollDetails.calculateMealAllowance()));
        addComponent(components, "transportAllowance", computeMonthlyIncome(payrollDetails.calculateTransportAllowance()));
        return components;
    }

    // 올해 설과 추석 구하는 메소드
    private Map<String, LocalDate> getGujeongAndChuseok(int currentYear) {
        KoreanLunarCalendar calendar = KoreanLunarCalendar.getInstance();
        // 윤달 여부
        boolean intercalation = calendar.isIntercalation();

        // 올해의 구정(설) 날짜 구하기
        calendar.setLunarDate(currentYear, 1, 1, intercalation);
        LocalDate gujeon = LocalDate.parse(calendar.getSolarIsoFormat());

        // 올해의 추석 양력 날짜 구하기
        calendar.setLunarDate(currentYear, 8, 15, intercalation);
        LocalDate chuseok = LocalDate.parse(calendar.getSolarIsoFormat());

        // 올해의 구정, 추석 날짜 반환
        return Map.of("gujeong", gujeon, "chuseok", chuseok);
    }

    // 성과급과 보너스 계산
    private Map<String, Integer> calculateBonusComponents(int annualSalary) {
        // 급여 구성항목의 비율 불러오기
        PayrollDetails payrollRatios = ratioDAO.selectPayrollRatios();

        // 새로운 PayrollDetails 객체 생성
        PayrollDetails payrollDetails = payrollRatios.toBuilder()
                .annualSalary(annualSalary)
                .build();

        // 올해의 구정과 추석 날짜 구하기
        LocalDate now = LocalDate.now();
        Map<String, LocalDate> holiday = getGujeongAndChuseok(now.getYear());
        int gujeongMonth = holiday.get("gujeong").getMonthValue();
        int chuseokMonth = holiday.get("chuseok").getMonthValue();

        // 해당 달에 맞게 보너스 계산
        Map<String, Integer> components = new HashMap<>();
        int previousMonth = now.getMonthValue() - 1;
        if (previousMonth == gujeongMonth || previousMonth == chuseokMonth) {
            // 명절 보너스 (1.25%)
            addComponent(components ,"holidayBonus", payrollDetails.calculateHolidayBonus());
        } else if (previousMonth == 6) {
            // 6월 : 팀 성과급 전반기 (1.25%)
            addComponent(components, "teamBonus", payrollDetails.calculateTeamBonus());
        } else if (previousMonth == 0) { // 1월의 이전 달은 12월이지만 -1하면 0이 나와서 0으로..
            // 12월 : 개인 성과급(2.5%), 팀 성과급(1.25%), 연말 보너스(2.5%)
            addComponent(components, "personalBonus", payrollDetails.calculatePersonalBonus());
            addComponent(components, "teamBonus", payrollDetails.calculateTeamBonus());
            addComponent(components, "yearEndBonus", payrollDetails.calculateYearEndBonus());
        }
        return components;
    }

    // 공제 항목(소득세, 국민연금, 건강보험, 장기요양보험, 고용보험) 계산
    private Map<String, Integer> calculateDeductions(int taxableSalary, String employeeId) {
        // 근로소득세 계산
        int thisMonthIncomeTax = calculateIncomeTax(taxableSalary, employeeId);

        // 공제 항목의 비율 불러오기
        DeductionDetails deductionRatios = ratioDAO.selectDeductionRatios();

        // 새로운 DeductionDetails 객체 생성
        DeductionDetails deductionDetails = deductionRatios.toBuilder()
                .taxableSalary(taxableSalary)
                .incomeTax(thisMonthIncomeTax)
                .build();

        // 공제 항목 비율로 계산하여 계산
        Map<String, Integer> components = new HashMap<>();
        addComponent(components, "incomeTax", deductionDetails.getIncomeTax());
        addComponent(components, "localIncomeTaxRate", deductionDetails.calculateLocalIncomeTax());
        addComponent(components, "nationalPensionRate", deductionDetails.calculateNationalPension());
        addComponent(components, "healthInsuranceRate", deductionDetails.calculateHealthInsurance());
        addComponent(components, "longTermCareInsurance", deductionDetails.calculateLongTermCareInsurance());
        addComponent(components, "employmentInsurance", deductionDetails.calculateEmploymentInsurance());
        return components;
    }

    // 근로소득세 계산
    private int calculateIncomeTax(int taxableSalary, String employeeId) {
        // 사원의 가족 정보 조회
        Map<String, Integer> familyInfo = employeeDAO.selectFamilyInfoById(employeeId);

        // 조회용 map
        Map<String , Object> map = new HashMap<>();
        map.put("numDependents", familyInfo.get("num_dependents"));
        map.put("taxableSalary", taxableSalary / 1000);

        // 간이세액표에서 해당하는 근로소득세 조회
        int income = ratioDAO.selectIncomeTax(map);

        // 근로소득세에서 가족 중 8세 이상 20세 이하 자녀 수만큼 공제
        income -= calculateChildTaxDeduction(familyInfo.get("num_children"));

        // 공제한 금액이 음수인 경우의 세액은 0원
        return Math.max(income, 0);
    }

    // 가족 중 8세 이상 20세 이하 자녀 수만큼 공제한 금액 계산
    private int calculateChildTaxDeduction(int numChildren) {
        if (numChildren >= 0 && numChildren <= 11) {
            return switch (numChildren) {
                case 0 -> 0;
                case 1 -> 12500;
                case 2 -> 29160;
                default -> 29160 + (25000 * (numChildren - 2));
            };
        } else {
            throw new IllegalArgumentException("자녀 수가 잘못되었습니다.");
        }
    }
}
