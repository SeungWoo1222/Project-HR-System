package com.woosan.hr_system.salary.controller.view;

import com.woosan.hr_system.auth.aspect.RequireHRPermission;
import com.woosan.hr_system.salary.model.Salary;
import com.woosan.hr_system.salary.model.SalaryPayment;
import com.woosan.hr_system.salary.service.SalaryPaymentService;
import com.woosan.hr_system.salary.service.SalaryService;
import com.woosan.hr_system.search.PageRequest;
import com.woosan.hr_system.search.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.YearMonth;
import java.util.List;

@Controller
@RequestMapping("/salary/payment")
public class SalaryPaymentViewController {
    @Autowired
    private SalaryService salaryService;
    @Autowired
    private SalaryPaymentService salaryPaymentService;

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

    @GetMapping("/{paymentId}") // 특정 사원의 급여 지급 내역 조회
    public String viewPayslip(@PathVariable int paymentId, Model model) {
        SalaryPayment payslip = salaryPaymentService.getPaymentById(paymentId);
        model.addAttribute("payslip", payslip);
        return "salary/payment/payslip";
    }

    @GetMapping("/employee/{employeeId}") // 특정 사원의 모든 급여 지급 내역 조회
    public String viewPayslipsByEmployeeId(@PathVariable String employeeId, Model model) {
        List<SalaryPayment> payslips = salaryPaymentService.getPaymentsByEmployeeId(employeeId);
        model.addAttribute("payslips", payslips);
        return "salary/payment/payslips";
    }

    @RequireHRPermission
    @GetMapping("/all") // 모든 급여 지급 내역 조회
    public String viewAllPayslip(Model model) {
        List<SalaryPayment> allPayslips = salaryPaymentService.getAllPayments();
        model.addAttribute("allPayslips", allPayslips);
        return "salary/payment/list";
    }
}
