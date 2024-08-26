package com.woosan.hr_system.salary.controller.api;

import com.woosan.hr_system.auth.aspect.RequireHRPermission;
import com.woosan.hr_system.auth.aspect.RequireManagerPermission;
import com.woosan.hr_system.salary.model.SalaryPayment;
import com.woosan.hr_system.salary.service.SalaryPaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/salary/payment")
public class SalaryPaymentApiController {
    @Autowired
    private SalaryPaymentService salaryPaymentService;

    @RequireHRPermission
    @PostMapping("/register") // 급여명세서 등록
    ResponseEntity<String> registerPayment(int salaryId) {
        return ResponseEntity.ok(salaryPaymentService.addPayment(salaryId));
    }
    @RequireHRPermission
    @PostMapping("/register/batch") // 다수의 급여명세서 등록
    ResponseEntity<String> registerPayments(@RequestParam(name = "yearmonth") String yearmonthString,
                                            @RequestParam(name= "salaryIdList") List<Integer> salaryIdList) {
        return ResponseEntity.ok(salaryPaymentService.addPayment(salaryIdList, yearmonthString));
    }

    @RequireHRPermission
    @PutMapping("/{paymentId}") // 급여명세서 수정
    ResponseEntity<String> updatePayment(@ModelAttribute SalaryPayment payslip) {
        return ResponseEntity.ok(salaryPaymentService.updatePayment(payslip));
    }

    @RequireHRPermission
    @RequireManagerPermission
    @DeleteMapping("/{paymentId}") // 급여명세서 삭제
    ResponseEntity<String> deletePayment(@PathVariable int paymentId) {
        return ResponseEntity.ok(salaryPaymentService.removePayment(paymentId));
    }
}
