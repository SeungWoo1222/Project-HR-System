package com.woosan.hr_system.salary.controller.view;

import com.woosan.hr_system.aspect.RequireHRPermission;
import com.woosan.hr_system.employee.model.Employee;
import com.woosan.hr_system.employee.service.EmployeeService;
import com.woosan.hr_system.salary.dao.RatioDAO;
import com.woosan.hr_system.salary.model.Salary;
import com.woosan.hr_system.salary.model.SalaryPayment;
import com.woosan.hr_system.salary.service.SalaryPaymentService;
import com.woosan.hr_system.salary.service.SalaryService;
import com.woosan.hr_system.search.PageRequest;
import com.woosan.hr_system.search.PageResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.YearMonth;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/salary/payment")
public class SalaryPaymentViewController {
    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private SalaryService salaryService;
    @Autowired
    private SalaryPaymentService salaryPaymentService;
    @Autowired
    private RatioDAO ratioDAO;

    @RequireHRPermission
    @GetMapping()// 급여 지급 페이지 이동
    public String viewPayList(@RequestParam(name = "page", defaultValue = "1") int page,
                              @RequestParam(name = "size", defaultValue = "10") int size,
                              @RequestParam(name = "keyword", defaultValue = "") String keyword,
                              @RequestParam(name = "department", defaultValue = "") String department,
                              @RequestParam(name = "yearmonth", defaultValue = "") String yearMonthString,
                              Model model) {
        // 해당 달 급여 지급 정보
        YearMonth yearMonth;
        if (yearMonthString.isEmpty()) {
            yearMonth = YearMonth.now();
        } else {
            yearMonth = YearMonth.parse(yearMonthString);
        }
        model.addAttribute("paymentStatus", salaryPaymentService.hasPaidSalaryThisMonth(yearMonth));

        // 검색 후 페이징
        PageRequest pageRequest = new PageRequest(page - 1, size, keyword); // 페이지 번호 인덱싱을 위해 다시 -1
        PageResult<Salary> pageResult = salaryService.searchUsingSalaries(pageRequest, department, yearMonth);

        model.addAttribute("salaries", pageResult.getData());
        model.addAttribute("currentPage", pageResult.getCurrentPage() + 1); // 뷰에서 가독성을 위해 +1
        model.addAttribute("totalPages", pageResult.getTotalPages());
        model.addAttribute("pageSize", size);
        model.addAttribute("keyword", keyword);
        model.addAttribute("department", department);
        model.addAttribute("yearmonth", yearMonth);

        return "salary/pay";
    }

    @RequireHRPermission
    @GetMapping("/confirm") // 급여 지급 전 급여 지급 목록 페이지 이동
    public String viewPayForm(@RequestParam(name = "yearmonth") String yearMonthString,
                              @RequestParam(name = "salaryIds") String salaryIds,
                              Model model) {
        List<Salary> salaries = salaryService.fetchSalaryListByIds(salaryIds);
        model.addAttribute("salaries", salaries);

        YearMonth yearMonth = YearMonth.parse(yearMonthString);
        model.addAttribute("yearmonth", yearMonth);
        return "salary/payment/pay-list";
    }

    @GetMapping("/{paymentId}") // 특정 사원의 급여 지급 내역 페이지 이동
    public String viewPayslip(@PathVariable("paymentId") int paymentId, Model model) {
        // 급여명세서 조회
        SalaryPayment payslip = salaryPaymentService.getPaymentById(paymentId);

        // 급여명세서에 급여 정보 삽입
        Salary salaryInfo = salaryService.getSalaryById(payslip.getSalaryId());
        payslip.setSalary(salaryInfo);
        model.addAttribute("payslip", payslip);

        // 급여명세서에 사원 정보 삽입
        Employee employee = employeeService.getEmployeeById(salaryInfo.getEmployeeId());
        model.addAttribute("employee", employee);
        return "salary/payment/payslip-modal";
    }

    @GetMapping("/{paymentId}/print") // 특정 사원의 급여 지급 내역 출력 및 pdf 변환 페이지 이동
    public String viewPayslipPrint(@PathVariable("paymentId") int paymentId, Model model) {
        // 급여명세서 조회
        SalaryPayment payslip = salaryPaymentService.getPaymentById(paymentId);

        // 급여명세서에 급여 정보 삽입
        Salary salaryInfo = salaryService.getSalaryById(payslip.getSalaryId());
        payslip.setSalary(salaryInfo);
        model.addAttribute("payslip", payslip);

        // 급여명세서에 사원 정보 삽입
        Employee employee = employeeService.getEmployeeById(salaryInfo.getEmployeeId());
        model.addAttribute("employee", employee);

        return "salary/payment/payslip-print";
    }

    @RequireHRPermission
    @GetMapping("/{paymentId}/edit") // 특정 사원의 급여 지급 수정 페이지 이동
    public String viewPayslipEditForm(@PathVariable("paymentId") int paymentId, Model model) {
        // 급여명세서 조회
        SalaryPayment payslip = salaryPaymentService.getPaymentById(paymentId);

        // 급여명세서에 급여 정보 삽입
        Salary salaryInfo = salaryService.getSalaryById(payslip.getSalaryId());
        payslip.setSalary(salaryInfo);
        model.addAttribute("payslip", payslip);

        // 급여명세서에 사원 정보 삽입
        Employee employee = employeeService.getEmployeeById(salaryInfo.getEmployeeId());
        model.addAttribute("employee", employee);
        return "salary/payment/payslip-edit";
    }

    @RequireHRPermission
    @GetMapping("/employee/{employeeId}") // 특정 사원의 모든 급여 지급 내역 조회
    public String viewPayslipsByEmployeeId(@PathVariable String employeeId, Model model) {
        List<SalaryPayment> payslips = salaryPaymentService.getPaymentsByEmployeeId(employeeId);
        model.addAttribute("payslips", payslips);
        return "salary/payment/payslips";
    }

    @RequireHRPermission
    @GetMapping("/list") // 모든 급여 지급 내역 조회
    public String viewAllPayslip(@RequestParam(name = "page", defaultValue = "1") int page,
                                 @RequestParam(name = "size", defaultValue = "10") int size,
                                 @RequestParam(name = "keyword", defaultValue = "") String keyword,
                                 @RequestParam(name = "department", defaultValue = "") String department,
                                 @RequestParam(name = "yearmonth", defaultValue = "") String yearMonthString,
                                 Model model) {
        YearMonth yearMonth;
        if (yearMonthString.isEmpty()) {
            yearMonth = YearMonth.now();
        } else {
            yearMonth = YearMonth.parse(yearMonthString);
        }

        // 검색 후 페이징
        PageRequest pageRequest = new PageRequest(page - 1, size, keyword); // 페이지 번호 인덱싱을 위해 다시 -1
        PageResult<SalaryPayment> pageResult = salaryPaymentService.searchPayslips(pageRequest, department, yearMonth);

        model.addAttribute("payslips", pageResult.getData());
        model.addAttribute("currentPage", pageResult.getCurrentPage() + 1); // 뷰에서 가독성을 위해 +1
        model.addAttribute("totalPages", pageResult.getTotalPages());
        model.addAttribute("pageSize", size);
        model.addAttribute("keyword", keyword);
        model.addAttribute("department", department);
        model.addAttribute("yearmonth", yearMonth);

        return "salary/payment/list";
    }

    @RequireHRPermission
    @GetMapping("/ratio") // 급여 및 공제 비율 조회
    public String viewRatio(Model model) {
        model.addAttribute("payrollRatios", ratioDAO.selectPayrollRatios());
        model.addAttribute("deductionRatios", ratioDAO.selectDeductionRatios());
        return "salary/payment/ratio";
    }

    @RequireHRPermission
    @GetMapping("/payroll/edit") // 급여 비율 수정 페이지 이동
    public String viewPayrollRatioEditForm(Model model) {
        model.addAttribute("payrollRatios", ratioDAO.selectPayrollRatios());
        return "salary/payment/payroll-edit";
    }

    @RequireHRPermission
    @GetMapping("/deduction/edit") // 공제 비율 수정 페이지 이동
    public String viewDeductionRatioEditForm(Model model) {
        model.addAttribute("deductionRatios", ratioDAO.selectDeductionRatios());
        return "salary/payment/deduction-edit";
    }

    @GetMapping("/myPayslips") // 내 급여명세서 조회
    public String viewMyPayslips(@RequestParam(name = "page", defaultValue = "1") int page,
                                 Model model) {
        final int size = 10;

        // 내 급여 정보
        String employeeId = SecurityContextHolder.getContext().getAuthentication().getName();
        List<Integer> salaryIds = salaryService.getSalaryIdList(employeeId);
        List<Salary> salaryList = salaryService.getSalariesByIds(salaryIds);
        model.addAttribute("salaryList", salaryList);

        // 내 급여명세서 검색 후 페이징
        PageRequest pageRequest = new PageRequest(page - 1, size); // 페이지 번호 인덱싱을 위해 다시 -1
        PageResult<SalaryPayment> pageResult = salaryPaymentService.searchMyPayslips(pageRequest, employeeId);

        model.addAttribute("payslips", pageResult.getData());
        model.addAttribute("currentPage", pageResult.getCurrentPage() + 1); // 뷰에서 가독성을 위해 +1
        model.addAttribute("totalPages", pageResult.getTotalPages());
        model.addAttribute("pageSize", size);

        return "salary/payment/payslips";
    }
}
