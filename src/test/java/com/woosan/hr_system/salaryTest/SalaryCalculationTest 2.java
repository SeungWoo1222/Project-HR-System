//package com.woosan.hr_system.salary;
//
//import com.woosan.hr_system.salary.dao.RatioDAO;
//import com.woosan.hr_system.salary.model.DeductionDetails;
//import com.woosan.hr_system.salary.model.PayrollDetails;
//import lombok.extern.slf4j.Slf4j;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import com.github.usingsky.calendar.KoreanLunarCalendar;
//
//import java.text.DecimalFormat;
//import java.time.LocalDate;
//import java.util.HashMap;
//import java.util.Map;
//
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//import static org.mockito.Mockito.when;
//
//@Slf4j
//public class SalaryCalculationTest {
//    private static final int annualSalary = 36123500;
//
//    @Mock
//    private RatioDAO ratioDAO;
//
//    @BeforeEach
//    public void setUp() {
//        MockitoAnnotations.openMocks(this);
//        PayrollDetails payrollDetails = new PayrollDetails();
//        payrollDetails.setBaseSalaryRatio(70.0);
//        payrollDetails.setPositionAllowanceRatio(10.0);
//        payrollDetails.setMealAllowanceRatio(5);
//        payrollDetails.setTransportAllowanceRatio(5);
//        payrollDetails.setPersonalBonusRatio(2.5);
//        payrollDetails.setTeamBonusRatio(2.5);
//        payrollDetails.setHolidayBonusRatio(2.5);
//        payrollDetails.setYearEndBonusRatio(2.5);
//
//        when(ratioDAO.selectPayrollRatios()).thenReturn(payrollDetails);
//
//        DeductionDetails deductionDetails = new DeductionDetails();
//        deductionDetails.setLocalIncomeTaxRate(10); // 적절한 값 설정
//        deductionDetails.setNationalPensionRate(4.5);
//        deductionDetails.setHealthInsuranceRate(3.545);
//        deductionDetails.setLongTermCareRate(0.4591);
//        deductionDetails.setEmploymentInsuranceRate(0.9);
//
//        when(ratioDAO.selectDeductionRatios()).thenReturn(deductionDetails);
//    }
//
//    // 연 수익 -> 월 수익으로 계산
//    private double computeMonthlyIncome(int income) {
//        return income / 12.0;
//    }
//
//    // 1원 단위는 반올림 처리
//    private int computeRoundIncome(double income) {
//        return ((int)Math.round(income / 10.0)) * 10;
//    }
//
//    // 특정 수당을 계산하고 Map에 추가하는 메서드
//    private void addComponent(Map<String, Integer> components, String key, double value) {
//        components.put(key, computeRoundIncome(value));
//    }
//
//    // 급여 구성 항목(기본급, 직책수당, 식대, 교통비) 계산
//    private Map<String, Integer> calculateSalaryComponents() {
//        PayrollDetails payrollRatios = ratioDAO.selectPayrollRatios();
//        PayrollDetails payrollDetails = payrollRatios.toBuilder()
//                .annualSalary(annualSalary)
//                .build();  // 새로운 PayrollDetails 객체 생성
//        log.info("payrollDetails: {}", payrollDetails);
//
//        Map<String, Integer> components = new HashMap<>();
//        addComponent(components, "baseSalary", computeMonthlyIncome(payrollDetails.calculateBaseSalary()));
//        addComponent(components, "positionAllowance", computeMonthlyIncome(payrollDetails.calculatePositionAllowance()));
//        addComponent(components, "mealAllowance", computeMonthlyIncome(payrollDetails.calculateMealAllowance()));
//        addComponent(components, "transportAllowance", computeMonthlyIncome(payrollDetails.calculateTransportAllowance()));
//        return components;
//    }
//
//    @Test
//    public void testCalculateSalaryComponents() {
//        Map<String, Integer> result = calculateSalaryComponents();
//        assertNotNull(result);
//
//        DecimalFormat formatter = new DecimalFormat("#,###");
//        for(String key : result.keySet()) {
//            String formattedValue = formatter.format(result.get(key));
//            log.info("{} : {}", key, formattedValue);
//        }
//    }
//
//    // 올해 설과 추석 구하는 메소드
//    private Map<String, LocalDate> getGujeongAndChuseok(int currentYear) {
//        KoreanLunarCalendar calendar = KoreanLunarCalendar.getInstance();
//        // 윤달 여부
//        boolean intercalation = calendar.isIntercalation();
//        // 올해의 구정(설) 날짜 구하기
//        calendar.setLunarDate(currentYear, 1, 1, intercalation);
//        LocalDate gujeon = LocalDate.parse(calendar.getSolarIsoFormat());
//        // 올해의 추석 양력 날짜 구하기
//        calendar.setLunarDate(currentYear, 8, 15, intercalation);
//        LocalDate chuseok = LocalDate.parse(calendar.getSolarIsoFormat());
//
//        return Map.of("gujeong", gujeon, "chuseok", chuseok);
//    }
//
//    // 성과급과 보너스 계산
//    private Map<String, Integer> calculateBonusComponents() {
//        PayrollDetails payrollRatios = ratioDAO.selectPayrollRatios();
//        PayrollDetails payrollDetails = payrollRatios.toBuilder()
//                .annualSalary(annualSalary)
//                .build();  // 새로운 PayrollDetails 객체 생성
//        log.info("payrollDetails: {}", payrollDetails);
//
//        LocalDate now = LocalDate.parse("2025-01-10");
//        Map<String, LocalDate> holiday = getGujeongAndChuseok(now.getYear());
//        log.info("설 : {}", holiday.get("gujeong"));
//        log.info("추석 : {}", holiday.get("chuseok"));
//
//        int gujeongMonth = holiday.get("gujeong").getMonthValue();
//        int chuseokMonth = holiday.get("chuseok").getMonthValue();
//
//        Map<String, Integer> components = new HashMap<>();
//        int previousMonth = now.getMonthValue() - 1;
//        if (previousMonth == gujeongMonth || previousMonth == chuseokMonth) {
//            // 명절 보너스 (1.25%)
//            addComponent(components ,"holidayBonus", payrollDetails.calculateHolidayBonus());
//        } else if (previousMonth == 6) {
//            // 6월 : 팀 성과급 전반기 (1.25%)
//            addComponent(components, "teamBonus", payrollDetails.calculateTeamBonus());
//        } else if (previousMonth == 0) { // 1월의 이전 달은 12월이지만 -1하면 0이 나와서 0으로..
//            // 12월 : 개인 성과급(2.5%), 팀 성과급(1.25%), 연말 보너스(2.5%)
//            addComponent(components, "personalBonus", payrollDetails.calculatePersonalBonus());
//            addComponent(components, "teamBonus", payrollDetails.calculateTeamBonus());
//            addComponent(components, "yearEndBonus", payrollDetails.calculateYearEndBonus());
//        }
//        return components;
//    }
//
//    @Test
//    public void testCalculateBonusComponents() {
//        Map<String, Integer> result = calculateBonusComponents();
//        assertNotNull(result);
//
//        DecimalFormat formatter = new DecimalFormat("#,###");
//        for(String key : result.keySet()) {
//            String formattedValue = formatter.format(result.get(key));
//            log.info("{} : {}", key, formattedValue);
//        }
//    }
//
//    // 공제 항목(소득세, 국민연금, 건강보험, 장기요양보험, 고용보험) 계산
//    private Map<String, Integer> calculateDeductions() {
//        // 비과세 미포함 월급
//        int taxableSalary = 3000000;
//        DeductionDetails deductionRatios = ratioDAO.selectDeductionRatios();
//
//        // 근로소득세 계산
////        int thisMonthIncomeTax = deductionRatios.calculateIncomeTax();
//
//        DeductionDetails deductionDetails = deductionRatios.toBuilder()
//                .taxableSalary(taxableSalary)
////                .incomeTax(thisMonthIncomeTax)
//                .build();
//
//        Map<String, Integer> components = new HashMap<>();
//        addComponent(components, "incomeTax", deductionDetails.getIncomeTax());
//        addComponent(components, "localIncomeTaxRate", deductionDetails.calculateLocalIncomeTax());
//        addComponent(components, "nationalPensionRate", deductionDetails.calculateNationalPension());
//        addComponent(components, "healthInsuranceRate", deductionDetails.calculateHealthInsurance());
//        addComponent(components, "longTermCareInsurance", deductionDetails.calculateLongTermCareInsurance());
//        addComponent(components, "employmentInsurance", deductionDetails.calculateEmploymentInsurance());
//        return components;
//    }
//
//    @Test
//    public void testCalculateDeductions() {
//        Map<String, Integer> result = calculateDeductions();
//        assertNotNull(result);
//
//        DecimalFormat formatter = new DecimalFormat("#,###");
//        for(String key : result.keySet()) {
//            String formattedValue = formatter.format(result.get(key));
//            log.info("{} : {}", key, formattedValue);
//        }
//    }
//
//}
