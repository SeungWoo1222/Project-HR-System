package com.woosan.hr_system.salary.controller.view;

import com.woosan.hr_system.salary.model.SalaryPayment;
import com.woosan.hr_system.salary.service.SalaryPaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/salary/payment/")
public class SalaryPaymentViewController {
    @Autowired
    private SalaryPaymentService salaryPaymentService;

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

    @GetMapping("/all") // 모든 급여 지급 내역 조회
    public String viewAllPayslip(Model model) {
        List<SalaryPayment> allPayslips = salaryPaymentService.getAllPayments();
        model.addAttribute("allPayslips", allPayslips);
        return "salary/payment/list";
    }
}
