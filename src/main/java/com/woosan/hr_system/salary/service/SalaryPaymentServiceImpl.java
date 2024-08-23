package com.woosan.hr_system.salary.service;

import com.woosan.hr_system.auth.aspect.LogAfterExecution;
import com.woosan.hr_system.auth.aspect.LogBeforeExecution;
import com.woosan.hr_system.common.service.CommonService;
import com.woosan.hr_system.employee.dao.EmployeeDAO;
import com.woosan.hr_system.salary.dao.SalaryDAO;
import com.woosan.hr_system.salary.dao.SalaryPaymentDAO;
import com.woosan.hr_system.salary.model.Salary;
import com.woosan.hr_system.salary.model.SalaryPayment;
import com.woosan.hr_system.search.PageRequest;
import com.woosan.hr_system.search.PageResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SalaryPaymentServiceImpl implements SalaryPaymentService {
    @Autowired
    private CommonService commonService;
    @Autowired
    private SalaryService salaryService;
    @Autowired
    private SalaryCalculation salaryCalculation;
    @Autowired
    private SalaryDAO salaryDAO;
    @Autowired
    private SalaryPaymentDAO salaryPaymentDAO;
    @Autowired
    private EmployeeDAO employeeDAO;

    @Override // 지급 ID를 이용한 특정 사원의 급여명세서 조회
    public SalaryPayment getPaymentById(int paymentId) {
        return salaryPaymentDAO.selectPaymentById(paymentId);
    }

    @Override // 사원 ID를 이용한 특정 사원의 모든 급여명세서 조회
    public List<SalaryPayment> getPaymentsByEmployeeId(String employeeId) {
        List<Integer> salaryIdList = salaryService.getSalaryIdList(employeeId);
        return salaryPaymentDAO.getPaymentsByEmployeeId(salaryIdList);
    }

    @Override // 모든 사원의 급여 정보 조회 (검색 기능 추가)
    public PageResult<SalaryPayment> searchPayslips(PageRequest pageRequest) {
        int offset = pageRequest.getPage() * pageRequest.getSize();
        List<SalaryPayment> payslips = salaryPaymentDAO.searchPayslips(pageRequest.getKeyword(), pageRequest.getSize(), offset);
        int total = salaryPaymentDAO.count(pageRequest.getKeyword());

        if (!payslips.isEmpty()) {
            // 급여명세서에 급여 정보 삽입
            setSalaryInfoToPayslips(payslips);
        }
        return new PageResult<>(payslips, (int) Math.ceil((double) total / pageRequest.getSize()), total, pageRequest.getPage());
    }

    // SalaryPayment에 Salary 정보 담기
    private void setSalaryInfoToPayslips(List<SalaryPayment> salaryPaymentList) {
        // 급여명세서에서 salaryId 추출 후 리스트로 변환
        List<Integer> salaryIdList = salaryPaymentList.stream()
                .map(SalaryPayment::getSalaryId)
                .toList();

        // 추출한 salaryIdList 이용하여 급여 정보 조회
        List<Salary> salaryList = salaryService.getSalariesByIds(salaryIdList);

        // 급여 정보를 맵으로 변환하여 salaryId를 키로 매핑
        Map<Integer, Salary> salaryMap = salaryList.stream()
                .collect(Collectors.toMap(Salary::getSalaryId, salary -> salary));

        // 급여 정보를 급여명세서에 삽입
        salaryPaymentList.forEach(salaryPayment ->
                salaryPayment.setSalary(salaryMap.get(salaryPayment.getSalaryId())));
    }

    @Override // 모든 급여명세서 조회
    public List<SalaryPayment> getAllPayments() {
        return salaryPaymentDAO.selectAllPayments();
    }

    @Override // salaryId와 yearMonth를 이용한 급여명세서 리스트 조회
    public List<SalaryPayment> getPaymentBySalaryAndMonth(List<Integer> salaryIdList, String yearMonthString) {
//        // 문자열 -> 리스트로 변환
//        String[] salaryIdArr = salaryIds.split(",");
//        Integer[] salaryIdArrInt = Arrays.stream(salaryIdArr).map(Integer::parseInt).toArray(Integer[]::new);
//        List<Integer> salaryIdList = Arrays.asList(salaryIdArrInt);

        // 문자열 -> YearMonth로 변환
        YearMonth yearMonth = YearMonth.parse(yearMonthString);

        return salaryPaymentDAO.selectPaymentBySalaryAndMonth(salaryIdList, yearMonth);
    }

    @LogBeforeExecution
    @LogAfterExecution
    @Override // 급여명세서 등록
    public String addPayment(int salaryId) {
        // 급여 정보 조회
        Salary salaryInfo = salaryService.getSalaryById(salaryId);

        // 급여명세서 생성 후 등록
        YearMonth yearMonth = YearMonth.now();
        SalaryPayment payslip = createPayslip(salaryInfo, yearMonth);
        salaryPaymentDAO.insertPayment(payslip);
        return "'" + employeeDAO.getEmployeeName(salaryInfo.getEmployeeId()) + "' 사원의 급여명세서가 등록되었습니다.";
    }
    @Transactional
    @LogBeforeExecution
    @LogAfterExecution
    @Override // 다수의 급여명세서 등록
    public String addPayment(List<Integer> salaryIdList, String yearmonthString) {
        YearMonth yearMonth = YearMonth.parse(yearmonthString);
        List<SalaryPayment> payslipList = new ArrayList<>();
        for (Integer salaryId : salaryIdList) {
            // 급여 정보 조회
            Salary salaryInfo = salaryService.getSalaryById(salaryId);

            // 급여명세서 생성 후 등록
            SalaryPayment payslip = createPayslip(salaryInfo, yearMonth);
            payslipList.add(payslip);
        }
        salaryPaymentDAO.insertPaymentList(payslipList);
        return (salaryIdList.size()) + "명의 사원에게 급여가 지급되었습니다.";
    }

    // 급여명세서 생성
    private SalaryPayment createPayslip(Salary salaryInfo, YearMonth yearMonth) {
        // 급여 항목 계산
        Map<String, Integer> payslipComponents = new HashMap<>();
        salaryCalculation.calculateSalaryPayment(salaryInfo.getEmployeeId(), salaryInfo.getAnnualSalary(), payslipComponents, yearMonth);
        log.info("'{}' 사원의 {}년 {}월 급여명세서가 생성되었습니다.", employeeDAO.getEmployeeName(salaryInfo.getEmployeeId()), yearMonth.getYear(), yearMonth.getMonthValue());

        // 급여명세서 작성
        return SalaryPayment.builder()
                .salaryId(salaryInfo.getSalaryId())
                .compensationMonth(yearMonth)
                .paymentDate(LocalDate.now())

                // 급여 구성 항목 설정
                .baseSalary(payslipComponents.get("baseSalary"))
                .positionAllowance(payslipComponents.get("positionAllowance"))
                .mealAllowance(payslipComponents.get("mealAllowance"))
                .transportAllowance(payslipComponents.get("transportAllowance"))

                // 보너스 항목 설정
                .personalBonus(payslipComponents.get("personalBonus"))
                .teamBonus(payslipComponents.get("teamBonus"))
                .holidayBonus(payslipComponents.get("holidayBonus"))
                .yearEndBonus(payslipComponents.get("yearEndBonus"))

                // 연장 수당 항목 설정
                .overtimePay(payslipComponents.get("overtimePay"))

                // 공제 항목 설정
                .incomeTax(payslipComponents.get("incomeTax"))
                .localIncomeTax(payslipComponents.get("localIncomeTax"))
                .nationalPension(payslipComponents.get("nationalPension"))
                .healthInsurance(payslipComponents.get("healthInsurance"))
                .longTermCareInsurance(payslipComponents.get("longTermCareInsurance"))
                .employmentInsurance(payslipComponents.get("employmentInsurance"))

                // 총 급여, 총 공제 금액, 실 지급액 계산 설정
                .grossSalary(payslipComponents.get("grossSalary"))
                .deductions(payslipComponents.get("deductions"))
                .netSalary(payslipComponents.get("grossSalary") - payslipComponents.get("deductions"))
                .build();
    }

    @LogBeforeExecution
    @LogAfterExecution
    @Transactional
    @Override // 급여명세서 수정
    public String updatePayment(SalaryPayment salaryPayment, int paymentId) {
        // 변경사항 확인
        SalaryPayment originalPayment = getPaymentById(paymentId);
        checkForPaymentChanges(originalPayment, salaryPayment);

        // 수정된 급여명세서 공제 항목 재계산
        Salary salaryInfo = salaryService.getSalaryById(salaryPayment.getSalaryId());
        SalaryPayment updatedPayment = salaryCalculation.calculateDeductions(salaryPayment, salaryInfo.getEmployeeId());

        // 급여명세서 수정
        salaryPaymentDAO.updatePayment(updatedPayment);
        return "급여명세서(" + paymentId + ")가 수정되었습니다.";
    }

    // Salary의 특정 필드만 비교하도록 필드 이름 Set으로 전달하는 메소드
    private void checkForPaymentChanges(SalaryPayment original, SalaryPayment updated) {
        Set<String> fieldsToCompare = new HashSet<>(Arrays.asList(
                "baseSalary", "positionAllowance", "mealAllowance", "transportAllowance",
                "personalBonus", "teamBonus", "holidayBonus", "yearEndBonus", "overtimePay", "remarks"
        ));
        commonService.processFieldChanges(original, updated, fieldsToCompare);
    }

    @LogBeforeExecution
    @LogAfterExecution
    @Override // 급여명세서 삭제
    public String removePayment(int paymentId) {
        // 급여 명세서 확인
        if (getPaymentById(paymentId) == null) {
            throw new IllegalArgumentException("해당 급여명세서를 찾을 수 없습니다.\n급여명세서 ID :" + paymentId);
        }

        // 삭제
        salaryPaymentDAO.deletePayment(paymentId);
        return "급여명세서(" + paymentId + ")가 삭제되었습니다.";
    }

    @Override // 해당 달 모든 사원의 급여 지급 여부 리스트 조회
    public Map<Integer, Boolean> hasPaidSalaryThisMonth(YearMonth yearMonth) {
        // 현재 사용중인 급여 정보 조회
        List<Integer> allSalaryIds = salaryDAO.selectUsingSalaryIdList();

        // 해당 월 지급된 급여내역 조회
        List<Integer> paidSalaryIds = salaryPaymentDAO.selectPaymentByMonth(yearMonth);

        // 두 리스트 비교하여 급여가 지급됐는지 여부 Map에 저장
        return allSalaryIds.stream()
                .collect(Collectors.toMap(
                        id -> id,
                        paidSalaryIds::contains
                ));
    }
}
